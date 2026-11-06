package com.example.quanlyphongtro;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import com.example.quanlyphongtro.model.ModelPhong;

public class QuanLyHoaDon extends AppCompatActivity {

    // Khai báo các biến giao diện (Tiền phòng + Phí dịch vụ tự động lấy từ điện nước)
    TextInputEditText txtMahoadon, txtTienphong, txtPhidichvu;
    AutoCompleteTextView txtMaphong;
    TextView txtTongtien;
    Button btnThem, btnXoa, btnQuayLaiHoaDon;
    ListView lvHoadon;

    QuanLyCoSoDuLieu dbHelper;
    double tongTienHienTai = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giaodienhoadon);

        dbHelper = new QuanLyCoSoDuLieu(this);

        // --- 1. ÁNH XẠ CÁC THÀNH PHẦN ---
        txtMahoadon = findViewById(R.id.txtmahoadon);
        txtMaphong = findViewById(R.id.txtmaphong);
        txtTienphong = findViewById(R.id.txttienphong);
        txtPhidichvu = findViewById(R.id.txtphidichvu);
        txtTongtien = findViewById(R.id.txttongtien);
        
        btnThem = findViewById(R.id.btnthem);
        btnXoa = findViewById(R.id.btnxoa);
        btnQuayLaiHoaDon = findViewById(R.id.btnQuayLaiHoaDon);
        lvHoadon = findViewById(R.id.lvhoadon);

        // Nạp danh sách các phòng đang cho thuê vào Dropdown chọn phòng
        loadDanhSachPhongDangThuê();

        // Load danh sách hóa đơn lên ListView ngay khi mở trang
        loadDanhSachHoaDon();

        // --- 2. KHI CHỌN PHÒNG -> TỰ ĐỘNG LẤY GIÁ THUÊ VÀ TỔNG TIỀN ĐIỆN NƯỚC DỊCH VỤ SANG ---
        txtMaphong.setOnItemClickListener((parent, view, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            String maPhong = selected;
            if (selected.contains(" ")) {
                maPhong = selected.split(" ")[0].trim();
            }

            // 1. Lấy giá thuê phòng từ CSDL
            int giaThue = dbHelper.getGiaThuePhong(maPhong);
            txtTienphong.setText(String.valueOf(giaThue));

            // 2. Lấy tổng tiền điện nước & dịch vụ đã ghi bên quản lý điện nước
            double tongDienNuocDichVu = dbHelper.getTongTienDienNuoc(maPhong);
            txtPhidichvu.setText(String.valueOf(tongDienNuocDichVu));

            // 3. Tự động cộng tổng tiền
            tinhTienTuDong();
        });

        // --- 3. SỰ KIỆN NÚT "XÁC NHẬN HÓA ĐƠN" (Lưu vào SQLite với trạng thái "Chưa thanh toán") ---
        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String maHD = txtMahoadon.getText().toString().trim();
                String maP = txtMaphong.getText().toString().trim();

                if (maHD.isEmpty() || maP.isEmpty() || maP.contains("Không có")) {
                    Toast.makeText(QuanLyHoaDon.this, "Vui lòng nhập mã hóa đơn và chọn phòng!", Toast.LENGTH_SHORT).show();
                    return;
                }

                tinhTienTuDong();
                boolean ketQua = dbHelper.themHoaDon(maHD, maP, tongTienHienTai, "Chưa thanh toán");
                if (ketQua) {
                    Toast.makeText(QuanLyHoaDon.this, "Xác nhận và lưu hóa đơn thành công!", Toast.LENGTH_SHORT).show();
                    loadDanhSachHoaDon();
                    xoaTrangForm();
                } else {
                    Toast.makeText(QuanLyHoaDon.this, "Lưu thất bại! Mã hóa đơn có thể bị trùng.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // --- 4. SỰ KIỆN NÚT "XÓA" (Làm mới form) ---
        btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xoaTrangForm();
                Toast.makeText(QuanLyHoaDon.this, "Đã làm mới form hóa đơn", Toast.LENGTH_SHORT).show();
            }
        });

        // --- 5. CLICK VÀO HÓA ĐƠN TRÊN LISTVIEW ĐỂ XÁC NHẬN THANH TOÁN ---
        lvHoadon.setOnItemClickListener((parent, view, position, id) -> {
            List<String> listHD = dbHelper.getAllHoaDonString();
            if (position < listHD.size()) {
                String itemText = listHD.get(position);
                String maHD = itemText.split("\\|")[0].replace("Mã HD:", "").trim();

                new AlertDialog.Builder(QuanLyHoaDon.this)
                        .setTitle("Cập nhật trạng thái thanh toán")
                        .setMessage("Bạn muốn đổi trạng thái của hóa đơn " + maHD + " thành 'Đã thanh toán'?")
                        .setPositiveButton("Đã thanh toán", (dialog, which) -> {
                            dbHelper.updateTrangThaiHoaDon(maHD, "Đã thanh toán");
                            Toast.makeText(QuanLyHoaDon.this, "Đã cập nhật: Đã thanh toán", Toast.LENGTH_SHORT).show();
                            loadDanhSachHoaDon();
                        })
                        .setNeutralButton("Chưa thanh toán", (dialog, which) -> {
                            dbHelper.updateTrangThaiHoaDon(maHD, "Chưa thanh toán");
                            Toast.makeText(QuanLyHoaDon.this, "Đã cập nhật: Chưa thanh toán", Toast.LENGTH_SHORT).show();
                            loadDanhSachHoaDon();
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });

        // --- 6. SỰ KIỆN NÚT "QUAY LẠI" ---
        btnQuayLaiHoaDon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void tinhTienTuDong() {
        try {
            double tienPhong = parseDoubleSafe(txtTienphong.getText().toString());
            double phiDichVu = parseDoubleSafe(txtPhidichvu.getText().toString());

            tongTienHienTai = tienPhong + phiDichVu;

            txtTongtien.setText("Tổng tiền: " + String.format(java.util.Locale.getDefault(), "%,.0f", tongTienHienTai) + " VNĐ");

        } catch (Exception ignored) {}
    }

    private double parseDoubleSafe(String val) {
        if (val == null || val.isEmpty()) return 0;
        try {
            return Double.parseDouble(val);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void loadDanhSachHoaDon() {
        List<String> danhSach = dbHelper.getAllHoaDonString();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, danhSach);
        lvHoadon.setAdapter(adapter);
    }

    private void xoaTrangForm() {
        txtMahoadon.setText("");
        txtMaphong.setText("", false);
        txtTienphong.setText("");
        txtPhidichvu.setText("");
        txtTongtien.setText("Tổng tiền: 0 VNĐ");
        tongTienHienTai = 0;
        txtMahoadon.requestFocus();
    }

    private void loadDanhSachPhongDangThuê() {
        List<ModelPhong> allPhong = dbHelper.getAllPhong();
        List<String> phongDangThue = new ArrayList<>();

        for (ModelPhong p : allPhong) {
            if (p.getTrangThai().toLowerCase().contains("đang thuê") || p.getTrangThai().toLowerCase().contains("thuê")) {
                phongDangThue.add(p.getMaPhong() + " (" + p.getTenPhong() + ")");
            }
        }

        if (phongDangThue.isEmpty()) {
            phongDangThue.add("Không có phòng đang cho thuê");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, phongDangThue);
        txtMaphong.setAdapter(adapter);
    }
}
