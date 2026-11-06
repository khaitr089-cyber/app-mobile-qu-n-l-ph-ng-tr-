package com.example.quanlyphongtro;

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

    // Khai báo các biến giao diện
    TextInputEditText txtMahoadon, txtDiencu, txtDienmoi, txtGiadien;
    TextInputEditText txtNuoccu, txtNuocmoi, txtGianuoc, txtTienphong, txtPhidichvu;
    AutoCompleteTextView txtMaphong;
    TextView txtTongtien;
    Button btnTinh, btnThem, btnXoa;
    ListView lvHoadon;

    QuanLyCoSoDuLieu dbHelper;
    double tongTienHienTai = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giaodienhoadon);

        dbHelper = new QuanLyCoSoDuLieu(this);

        // 1. Ánh xạ các thành phần từ XML sang Java
        txtMahoadon = findViewById(R.id.txtmahoadon);
        txtMaphong = findViewById(R.id.txtmaphong);
        
        txtDiencu = findViewById(R.id.txtdiencu);
        txtDienmoi = findViewById(R.id.txtdienmoi);
        txtGiadien = findViewById(R.id.txtgiadien);
        
        txtNuoccu = findViewById(R.id.txtnuoccu);
        txtNuocmoi = findViewById(R.id.txtnuocmoi);
        txtGianuoc = findViewById(R.id.txtgianuoc);
        
        txtTienphong = findViewById(R.id.txttienphong);
        txtPhidichvu = findViewById(R.id.txtphidichvu);
        
        txtTongtien = findViewById(R.id.txttongtien);
        
        btnTinh = findViewById(R.id.btntinh);
        btnThem = findViewById(R.id.btnthem);
        btnXoa = findViewById(R.id.btnxoa);
        lvHoadon = findViewById(R.id.lvhoadon);

        // Nạp danh sách các phòng đang cho thuê vào Dropdown chọn phòng
        loadDanhSachPhongDangThuê();

        // Load danh sách hóa đơn lên ListView ngay khi mở trang
        loadDanhSachHoaDon();

        // Sự kiện khi người dùng chọn phòng từ dropdown -> Tự động điền số tiền phòng
        txtMaphong.setOnItemClickListener((parent, view, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            String maPhong = selected;
            if (selected.contains(" ")) {
                maPhong = selected.split(" ")[0].trim();
            }
            int giaThue = dbHelper.getGiaThuePhong(maPhong);
            txtTienphong.setText(String.valueOf(giaThue));
        });

        // 2. Sự kiện bấm nút "Tính tiền"
        btnTinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tinhTienHoaDon();
            }
        });

        // 3. Sự kiện nút "Lưu" (Thêm hóa đơn vào SQLite)
        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String maHD = txtMahoadon.getText().toString().trim();
                String maP = txtMaphong.getText().toString().trim();

                if (maHD.isEmpty() || maP.isEmpty()) {
                    Toast.makeText(QuanLyHoaDon.this, "Vui lòng nhập mã hóa đơn và chọn phòng!", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean ketQua = dbHelper.themHoaDon(maHD, maP, tongTienHienTai);
                if (ketQua) {
                    Toast.makeText(QuanLyHoaDon.this, "Lưu hóa đơn thành công!", Toast.LENGTH_SHORT).show();
                    loadDanhSachHoaDon();
                    xoaTrangForm();
                } else {
                    Toast.makeText(QuanLyHoaDon.this, "Lưu thất bại! Mã hóa đơn có thể bị trùng.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 4. Sự kiện nút "Xóa" (Làm mới form)
        btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xoaTrangForm();
            }
        });
    }

    private void tinhTienHoaDon() {
        try {
            double dienCu = Double.parseDouble(txtDiencu.getText().toString());
            double dienMoi = Double.parseDouble(txtDienmoi.getText().toString());
            double giaDien = Double.parseDouble(txtGiadien.getText().toString());

            double nuocCu = Double.parseDouble(txtNuoccu.getText().toString());
            double nuocMoi = Double.parseDouble(txtNuocmoi.getText().toString());
            double giaNuoc = Double.parseDouble(txtGianuoc.getText().toString());

            double tienPhong = Double.parseDouble(txtTienphong.getText().toString());
            double phiDichVu = Double.parseDouble(txtPhidichvu.getText().toString());

            double tienDien = (dienMoi - dienCu) * giaDien;
            double tienNuoc = (nuocMoi - nuocCu) * giaNuoc;
            tongTienHienTai = tienPhong + tienDien + tienNuoc + phiDichVu;

            txtTongtien.setText("Tổng tiền: " + tongTienHienTai + " VNĐ");

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ và chính xác các con số!", Toast.LENGTH_SHORT).show();
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
        txtDiencu.setText("");
        txtDienmoi.setText("");
        txtGiadien.setText("");
        txtNuoccu.setText("");
        txtNuocmoi.setText("");
        txtGianuoc.setText("");
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
