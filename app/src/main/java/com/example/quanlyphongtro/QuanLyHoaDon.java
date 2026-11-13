package com.example.quanlyphongtro;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import java.util.Locale;

import com.example.quanlyphongtro.model.ModelPhong;

public class QuanLyHoaDon extends AppCompatActivity {

    TextInputEditText txtMahoadon, txtTienphong, txtPhidichvu;
    AutoCompleteTextView txtMaphong;
    TextView txtTongtien;
    Button btnThem, btnVietQR, btnXoa, btnQuayLaiHoaDon;
    ListView lvHoadon;

    QuanLyCoSoDuLieu dbHelper;
    double tongTienHienTai = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giaodienhoadon);

        dbHelper = new QuanLyCoSoDuLieu(this);

        // --- 1. ÁNH XẠ GIAO DIỆN ---
        txtMahoadon = findViewById(R.id.txtmahoadon);
        txtMaphong = findViewById(R.id.txtmaphong);
        txtTienphong = findViewById(R.id.txttienphong);
        txtPhidichvu = findViewById(R.id.txtphidichvu);
        txtTongtien = findViewById(R.id.txttongtien);

        btnThem = findViewById(R.id.btnthem);
        btnVietQR = findViewById(R.id.btnVietQR);
        btnXoa = findViewById(R.id.btnxoa);
        btnQuayLaiHoaDon = findViewById(R.id.btnQuayLaiHoaDon);
        lvHoadon = findViewById(R.id.lvhoadon);

        // Nạp danh sách các phòng đang cho thuê
        loadDanhSachPhongDangThue();

        // Load danh sách hóa đơn lên ListView
        loadDanhSachHoaDon();

        // --- 2. LẮP BỘ LẮNG NGHE KHI CHỌN PHÒNG -> TỰ ĐỘNG TẢI TIỀN PHÒNG & TIỀN ĐIỆN NƯỚC SANG ---
        txtMaphong.setOnItemClickListener((parent, view, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            String maPhong = selected;
            if (selected.contains(" ")) {
                maPhong = selected.split(" ")[0].trim();
            }

            // A. Lấy giá thuê phòng từ CSDL
            int giaThue = dbHelper.getGiaThuePhong(maPhong);
            txtTienphong.setText(String.valueOf(giaThue));

            // B. Tự động lôi tổng tiền điện nước đã chốt bên trang Quản lý Điện Nước
            double tongDienNuocDichVu = dbHelper.getTongTienDienNuoc(maPhong);
            txtPhidichvu.setText(String.valueOf((long) tongDienNuocDichVu));

            // Tự động tính ra tổng tiền hóa đơn ngay lập tức
            tinhTienTuDong();
            Toast.makeText(QuanLyHoaDon.this, "Đã tự động tải tiền phòng và chỉ số điện nước mới nhất!", Toast.LENGTH_SHORT).show();
        });

        // Bổ sung TextWatcher để khi sửa tiền phòng hoặc phí dịch vụ thì tổng tiền tự đổi
        TextWatcher tw = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                tinhTienTuDong();
            }
            @Override public void afterTextChanged(Editable s) {}
        };
        txtTienphong.addTextChangedListener(tw);
        txtPhidichvu.addTextChangedListener(tw);

        // --- 3. SỰ KIỆN NÚT "LƯU & XÁC NHẬN HÓA ĐƠN" ---
        btnThem.setOnClickListener(v -> {
            String maHD = txtMahoadon.getText() != null ? txtMahoadon.getText().toString().trim() : "";
            String maP = txtMaphong.getText().toString().trim();

            if (maHD.isEmpty() || maP.isEmpty() || maP.contains("Không có")) {
                Toast.makeText(QuanLyHoaDon.this, "Vui lòng nhập mã hóa đơn và chọn phòng!", Toast.LENGTH_SHORT).show();
                return;
            }

            tinhTienTuDong();
            boolean ketQua = dbHelper.themHoaDon(maHD, maP, tongTienHienTai, "Chưa thanh toán");
            if (ketQua) {
                Toast.makeText(QuanLyHoaDon.this, "Lưu và khởi tạo hóa đơn thành công!", Toast.LENGTH_SHORT).show();
                loadDanhSachHoaDon();
                xoaTrangForm();
            } else {
                Toast.makeText(QuanLyHoaDon.this, "Lưu thất bại! Mã hóa đơn có thể đã bị trùng.", Toast.LENGTH_SHORT).show();
            }
        });

        // --- 4. SỰ KIỆN NÚT "TẠO MÃ VIETQR & GỬI ZALO/SMS" ---
        btnVietQR.setOnClickListener(v -> {
            String maHD = txtMahoadon.getText() != null ? txtMahoadon.getText().toString().trim() : "HD-MOI";
            String maP = txtMaphong.getText().toString().trim();

            if (maP.isEmpty() || maP.contains("Không có")) {
                Toast.makeText(QuanLyHoaDon.this, "Vui lòng chọn phòng trước khi tạo mã QR!", Toast.LENGTH_SHORT).show();
                return;
            }

            tinhTienTuDong();
            String qrUrl = VietQRHelper.taoUrlVietQR("MB", "0987654321", "CHU TRO", tongTienHienTai, "Thanh toan tien tro " + maHD);
            
            hienThiDialogVietQR(maHD, maP, tongTienHienTai, qrUrl);
        });

        // --- 5. SỰ KIỆN NÚT "LÀM MỚI FORM" ---
        btnXoa.setOnClickListener(v -> xoaTrangForm());

        // --- 6. CLICK VÀO HÓA ĐƠN TRÊN LISTVIEW ĐỂ XEM CHI TIẾT & CHIA SẺ ZALO ---
        lvHoadon.setOnItemClickListener((parent, view, position, id) -> {
            List<String> listHD = dbHelper.getAllHoaDonString();
            if (position < listHD.size()) {
                String itemText = listHD.get(position);
                String maHD = itemText.split("\\|")[0].replace("Mã HD:", "").trim();
                String maPhong = itemText.contains("Phòng:") ? itemText.split("\\|")[1].replace("Phòng:", "").trim() : "";

                String qrUrl = VietQRHelper.taoUrlVietQR("MB", "0987654321", "CHU TRO", 3000000, "Thanh toan " + maHD);

                new AlertDialog.Builder(QuanLyHoaDon.this)
                        .setTitle("Chi tiết Hóa đơn: " + maHD)
                        .setMessage("Phòng: " + maPhong + "\nTrạng thái: " + itemText + "\n\nĐường dẫn mã VietQR:\n" + qrUrl)
                        .setPositiveButton("Đánh dấu ĐÃ THANH TOÁN", (dialog, which) -> {
                            dbHelper.updateTrangThaiHoaDon(maHD, "Đã thanh toán");
                            Toast.makeText(QuanLyHoaDon.this, "Đã đổi trạng thái sang Đã thanh toán!", Toast.LENGTH_SHORT).show();
                            loadDanhSachHoaDon();
                        })
                        .setNeutralButton("📲 Gửi Zalo/SMS 1-Click", (dialog, which) -> {
                            guiZaloNhanh(maHD, maPhong, qrUrl);
                        })
                        .setNegativeButton("Đóng", null)
                        .show();
            }
        });

        // --- 7. SỰ KIỆN NÚT "QUAY LẠI" ---
        btnQuayLaiHoaDon.setOnClickListener(v -> finish());
    }

    private void hienThiDialogVietQR(String maHD, String maPhong, double soTien, String qrUrl) {
        String msg = "🏠 HÓA ĐƠN TIỀN TRỌ " + maHD.toUpperCase() + "\n"
                + "📍 Phòng: " + maPhong + "\n"
                + "💰 Tổng thanh toán: " + String.format(Locale.getDefault(), "%,.0f VNĐ", soTien) + "\n\n"
                + "🔗 Đường dẫn mã VietQR (Napas247):\n" + qrUrl;

        new AlertDialog.Builder(this)
                .setTitle("MÃ VIETQR & BÌNH LẬP HÓA ĐƠN")
                .setMessage(msg)
                .setPositiveButton("📲 GỬI QUA ZALO KHÁCH", (dialog, which) -> {
                    guiZaloNhanh(maHD, maPhong, qrUrl);
                })
                .setNegativeButton("Đóng", null)
                .show();
    }

    private void guiZaloNhanh(String maHD, String maPhong, String qrUrl) {
        String message = "Kính gửi khách thuê phòng " + maPhong + ",\n"
                + "Đây là thông báo hóa đơn tiền nhà " + maHD + ".\n"
                + "Tổng số tiền: " + String.format(Locale.getDefault(), "%,.0f VNĐ", tongTienHienTai) + "\n"
                + "Vui lòng quét mã VietQR để thanh toán:\n" + qrUrl + "\nXin cảm ơn!";

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(shareIntent, "Gửi thông báo hóa đơn qua Zalo/SMS"));
    }

    private void tinhTienTuDong() {
        try {
            double tienPhong = parseDoubleSafe(txtTienphong.getText() != null ? txtTienphong.getText().toString() : "0");
            double phiDichVu = parseDoubleSafe(txtPhidichvu.getText() != null ? txtPhidichvu.getText().toString() : "0");

            tongTienHienTai = tienPhong + phiDichVu;

            txtTongtien.setText("TỔNG TIỀN: " + String.format(Locale.getDefault(), "%,.0f VNĐ", tongTienHienTai));
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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_list_row, danhSach);
        lvHoadon.setAdapter(adapter);
    }

    private void xoaTrangForm() {
        if (txtMahoadon != null) txtMahoadon.setText("");
        if (txtMaphong != null) txtMaphong.setText("", false);
        if (txtTienphong != null) txtTienphong.setText("");
        if (txtPhidichvu != null) txtPhidichvu.setText("");
        if (txtTongtien != null) txtTongtien.setText("TỔNG TIỀN: 0 VNĐ");
        tongTienHienTai = 0;
        if (txtMahoadon != null) txtMahoadon.requestFocus();
    }

    private void loadDanhSachPhongDangThue() {
        List<ModelPhong> allPhong = dbHelper.getAllPhong();
        List<String> phongDangThue = new ArrayList<>();

        for (ModelPhong p : allPhong) {
            if (p.getTrangThai() != null && (p.getTrangThai().toLowerCase().contains("đang thuê") || p.getTrangThai().toLowerCase().contains("thuê"))) {
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
