package com.example.quanlyphongtro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.example.quanlyphongtro.adapter.DienNuocAdapter;
import com.example.quanlyphongtro.model.ModelDienNuoc;
import com.example.quanlyphongtro.model.ModelPhong;

public class QuanLyDienNuoc extends AppCompatActivity {

    // Khai báo các view
    AutoCompleteTextView dropdownChonPhongDienNuoc;
    TextInputEditText edtThangGhi, edtDienCu, edtDienMoi, edtNuocCu, edtNuocMoi, edtTienDichVuKhac, edtTimKiemDienNuoc;
    TextView tvTongTienTinhToan;
    Button btnXacNhanDienNuoc, btnLamMoiDienNuoc, btnCaiDatGia, btnQuayLaiDienNuoc;
    RecyclerView recyclerDanhSachDienNuoc;

    QuanLyCoSoDuLieu db;
    List<ModelDienNuoc> danhSachDienNuoc;
    DienNuocAdapter dienNuocAdapter;

    // Đơn giá mặc định cho điện và nước
    private final double donGiaDien = 3500; // VNĐ / kWh
    private final double donGiaNuoc = 10000; // VNĐ / m3

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giaodiendiennuocdichvu);

        db = new QuanLyCoSoDuLieu(this);

        // Ánh xạ View
        dropdownChonPhongDienNuoc = findViewById(R.id.dropdownChonPhongDienNuoc);
        edtThangGhi = findViewById(R.id.edtThangGhi);
        edtDienCu = findViewById(R.id.edtDienCu);
        edtDienMoi = findViewById(R.id.edtDienMoi);
        edtNuocCu = findViewById(R.id.edtNuocCu);
        edtNuocMoi = findViewById(R.id.edtNuocMoi);
        edtTienDichVuKhac = findViewById(R.id.edtTienDichVuKhac);
        tvTongTienTinhToan = findViewById(R.id.tvTongTienTinhToan);

        btnXacNhanDienNuoc = findViewById(R.id.btnXacNhanDienNuoc);
        btnLamMoiDienNuoc = findViewById(R.id.btnLamMoiDienNuoc);
        btnCaiDatGia = findViewById(R.id.btnCaiDatGia);
        btnQuayLaiDienNuoc = findViewById(R.id.btnQuayLaiDienNuoc);
        edtTimKiemDienNuoc = findViewById(R.id.edtTimKiemDienNuoc);
        recyclerDanhSachDienNuoc = findViewById(R.id.recyclerDanhSachDienNuoc);

        // Khởi tạo danh sách ban đầu từ CSDL
        loadDanhSachDienNuoc();

        // Nạp danh sách phòng vào dropdown
        loadDanhSachPhong();

        dropdownChonPhongDienNuoc.setOnItemClickListener((parent, view, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            if (selected != null && !selected.contains("Không có")) {
                String maPhong = selected.split(" ")[0].trim();
                double[] chisoCu = db.getChisoDienNuocMoiNhatByMaPhong(maPhong);
                if (chisoCu[0] > 0 || chisoCu[1] > 0) {
                    edtDienCu.setText(String.valueOf((long) chisoCu[0]));
                    edtNuocCu.setText(String.valueOf((long) chisoCu[1]));
                    Toast.makeText(this, "Đã tự động nạp chỉ số điện/nước cũ tháng trước!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Tự động tính tiền khi người dùng nhập liệu chỉ số cũ/mới hoặc dịch vụ
        TextWatcher boTheoDoiNhapLieu = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tinhTongTienTamTinh();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        edtDienCu.addTextChangedListener(boTheoDoiNhapLieu);
        edtDienMoi.addTextChangedListener(boTheoDoiNhapLieu);
        edtNuocCu.addTextChangedListener(boTheoDoiNhapLieu);
        edtNuocMoi.addTextChangedListener(boTheoDoiNhapLieu);
        edtTienDichVuKhac.addTextChangedListener(boTheoDoiNhapLieu);

        // --- TÍNH NĂNG TÌM KIẾM ---
        edtTimKiemDienNuoc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                timKiemDienNuoc(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Nút cài đặt đơn giá điện nước
        btnCaiDatGia.setOnClickListener(v -> hienThiDialogCaiDatGia());

        // Nút XÁC NHẬN (Lưu/Cập nhật điện nước)
        btnXacNhanDienNuoc.setOnClickListener(v -> {
            String phong = dropdownChonPhongDienNuoc.getText().toString().trim();
            String thang = edtThangGhi.getText() != null ? edtThangGhi.getText().toString().trim() : "";

            if (phong.isEmpty() || thang.isEmpty() || phong.contains("Không có")) {
                Toast.makeText(this, "Vui lòng chọn phòng và nhập tháng ghi!", Toast.LENGTH_SHORT).show();
                return;
            }

            double dienCu = chuyenDinhDangSo(edtDienCu.getText().toString().trim());
            double dienMoi = chuyenDinhDangSo(edtDienMoi.getText().toString().trim());
            double nuocCu = chuyenDinhDangSo(edtNuocCu.getText().toString().trim());
            double nuocMoi = chuyenDinhDangSo(edtNuocMoi.getText().toString().trim());

            if (dienMoi < dienCu || nuocMoi < nuocCu) {
                Toast.makeText(this, "Chỉ số điện/nước mới không được nhỏ hơn chỉ số cũ!", Toast.LENGTH_SHORT).show();
                return;
            }
            double soDienTieuThu = laySoDienTieuThu();
            double soNuocTieuThu = laySoNuocTieuThu();
            double tienDichVu = chuyenDinhDangSo(edtTienDichVuKhac.getText().toString().trim());
            double tongTien = (soDienTieuThu * donGiaDien) + (soNuocTieuThu * donGiaNuoc) + tienDichVu;

            String maClean = phong.split(" ")[0].trim();
            boolean saveDb = db.themDienNuoc(maClean, thang, dienCu, dienMoi, nuocCu, nuocMoi, tienDichVu, tongTien);
            
            if (saveDb) {
                loadDanhSachDienNuoc();
                Toast.makeText(this, "Xác nhận và lưu chỉ số điện nước vào CSDL thành công!", Toast.LENGTH_SHORT).show();
                lamMoiForm();
            } else {
                Toast.makeText(this, "Lưu vào CSDL thất bại!", Toast.LENGTH_SHORT).show();
            }
        });

        // Nút Làm mới form
        btnLamMoiDienNuoc.setOnClickListener(v -> lamMoiForm());

        // Nút Quay lại
        btnQuayLaiDienNuoc.setOnClickListener(v -> finish());
    }

    private void loadDanhSachDienNuoc() {
        danhSachDienNuoc = db.getAllDienNuoc();
        if (danhSachDienNuoc == null) danhSachDienNuoc = new ArrayList<>();
        hienThiDanhSach(danhSachDienNuoc);
    }

    // --- TÍNH SỐ TIÊU THỤ ---
    private double laySoDienTieuThu() {
        try {
            double cu = Double.parseDouble(edtDienCu.getText().toString().trim());
            double moi = Double.parseDouble(edtDienMoi.getText().toString().trim());
            return Math.max(0, moi - cu);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private double laySoNuocTieuThu() {
        try {
            double cu = Double.parseDouble(edtNuocCu.getText().toString().trim());
            double moi = Double.parseDouble(edtNuocMoi.getText().toString().trim());
            return Math.max(0, moi - cu);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private double chuyenDinhDangSo(String text) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // --- TỰ ĐỘNG TÍNH TIỀN ---
    private void tinhTongTienTamTinh() {
        double soDien = laySoDienTieuThu();
        double soNuoc = laySoNuocTieuThu();
        double tienDichVu = chuyenDinhDangSo(edtTienDichVuKhac.getText().toString().trim());

        double tongTien = (soDien * donGiaDien) + (soNuoc * donGiaNuoc) + tienDichVu;
        tvTongTienTinhToan.setText(String.format(Locale.getDefault(), "Tổng tiền tạm tính: %,.0f VNĐ", tongTien));
    }

    // --- TÌM KIẾM ĐIỆN NƯỚC (Không fill lại form khi click) ---
    private void timKiemDienNuoc(String tuKhoa) {
        String tuKhoaChuan = tuKhoa.toLowerCase().trim();
        List<ModelDienNuoc> danhSachKetQua = new ArrayList<>();

        for (ModelDienNuoc item : danhSachDienNuoc) {
            if (item.getMaPhong().toLowerCase().contains(tuKhoaChuan) ||
                item.getThangGhi().toLowerCase().contains(tuKhoaChuan)) {
                danhSachKetQua.add(item);
            }
        }

        dienNuocAdapter = new DienNuocAdapter(this, danhSachKetQua, null);
        recyclerDanhSachDienNuoc.setAdapter(dienNuocAdapter);
    }

    // --- CÀI ĐẶT ĐƠN GIÁ ---
    private void hienThiDialogCaiDatGia() {
        Toast.makeText(this, "Đơn giá hiện tại: Điện = " + donGiaDien + "đ/kWh | Nước = " + donGiaNuoc + "đ/m3", Toast.LENGTH_LONG).show();
    }

    // --- LOAD PHÒNG ---
    private void loadDanhSachPhong() {
        List<ModelPhong> tatCaPhong = db.getAllPhong();
        List<String> tenPhongList = new ArrayList<>();
        for (ModelPhong p : tatCaPhong) {
            tenPhongList.add(p.getMaPhong() + " - " + p.getTenPhong());
        }
        if (tenPhongList.isEmpty()) {
            tenPhongList.add("Không có phòng nào");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, tenPhongList);
        dropdownChonPhongDienNuoc.setAdapter(adapter);
    }

    // --- HIỂN THỊ DANH SÁCH (Không fill lại form khi click) ---
    private void hienThiDanhSach(List<ModelDienNuoc> list) {
        dienNuocAdapter = new DienNuocAdapter(this, list, null);
        recyclerDanhSachDienNuoc.setLayoutManager(new LinearLayoutManager(this));
        recyclerDanhSachDienNuoc.setAdapter(dienNuocAdapter);
    }

    // --- LÀM MỚI FORM ---
    private void lamMoiForm() {
        dropdownChonPhongDienNuoc.setText("", false);
        edtThangGhi.setText("");
        edtDienCu.setText("");
        edtDienMoi.setText("");
        edtNuocCu.setText("");
        edtNuocMoi.setText("");
        edtTienDichVuKhac.setText("");
        tvTongTienTinhToan.setText("Tổng tiền tạm tính: 0 VNĐ");
        dropdownChonPhongDienNuoc.requestFocus();
    }
}
