package com.example.quanlyphongtro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.quanlyphongtro.adapter.DienNuocAdapter;
import com.example.quanlyphongtro.model.ModelDienNuoc;
import com.example.quanlyphongtro.model.ModelPhong;

public class QuanLyDienNuoc extends AppCompatActivity {

    AutoCompleteTextView dropdownChonPhongDienNuoc;
    TextInputEditText edtThangGhi, edtDienCu, edtDienMoi, edtNuocCu, edtNuocMoi, edtTienDichVuKhac, edtTimKiemDienNuoc;
    TextView tvTongTienTinhToan;
    Button btnXacNhanDienNuoc, btnLamMoiDienNuoc, btnCaiDatGia, btnQuayLaiDienNuoc;
    RecyclerView recyclerDanhSachDienNuoc;

    QuanLyCoSoDuLieu db;
    List<ModelDienNuoc> danhSachDienNuoc;
    DienNuocAdapter dienNuocAdapter;

    private final double donGiaDien = 3500;
    private final double donGiaNuoc = 10000;

    // Executor & Handler
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

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

        loadDanhSachDienNuoc();
        loadDanhSachPhong();

        dropdownChonPhongDienNuoc.setOnItemClickListener((parent, view, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            if (selected != null && !selected.contains("Không có")) {
                String maPhong = selected.split(" ")[0].trim();
                executor.execute(() -> {
                    double[] chisoCu = db.getChisoDienNuocMoiNhatByMaPhong(maPhong);
                    mainHandler.post(() -> {
                        if (chisoCu[0] > 0 || chisoCu[1] > 0) {
                            edtDienCu.setText(String.valueOf((long) chisoCu[0]));
                            edtNuocCu.setText(String.valueOf((long) chisoCu[1]));
                            Toast.makeText(this, "Đã tự động nạp chỉ số điện/nước cũ tháng trước!", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            }
        });

        TextWatcher boTheoDoiNhapLieu = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                tinhTongTienTamTinh();
            }
            @Override public void afterTextChanged(Editable s) {}
        };

        edtDienCu.addTextChangedListener(boTheoDoiNhapLieu);
        edtDienMoi.addTextChangedListener(boTheoDoiNhapLieu);
        edtNuocCu.addTextChangedListener(boTheoDoiNhapLieu);
        edtNuocMoi.addTextChangedListener(boTheoDoiNhapLieu);
        edtTienDichVuKhac.addTextChangedListener(boTheoDoiNhapLieu);

        // DEBOUNCE 250MS TÌM KIẾM
        Handler debounceHandler = new Handler(Looper.getMainLooper());
        Runnable searchRunnable = () -> {
            String kw = edtTimKiemDienNuoc.getText() != null ? edtTimKiemDienNuoc.getText().toString() : "";
            timKiemDienNuoc(kw);
        };

        edtTimKiemDienNuoc.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                debounceHandler.removeCallbacks(searchRunnable);
                debounceHandler.postDelayed(searchRunnable, 250);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnCaiDatGia.setOnClickListener(v -> hienThiDialogCaiDatGia());

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

            double soDienTieuThu = laySoDienTieuThu();
            double soNuocTieuThu = laySoNuocTieuThu();
            double tienDichVu = chuyenDinhDangSo(edtTienDichVuKhac.getText().toString().trim());
            double tienDien = tinhTienDienBacThang(soDienTieuThu);
            double tongTien = tienDien + (soNuocTieuThu * donGiaNuoc) + tienDichVu;

            String maClean = phong.split(" ")[0].trim();

            executor.execute(() -> {
                boolean saveDb = db.themDienNuoc(maClean, thang, dienCu, dienMoi, nuocCu, nuocMoi, tienDichVu, tongTien);
                mainHandler.post(() -> {
                    if (saveDb) {
                        loadDanhSachDienNuoc();
                        Toast.makeText(this, "Xác nhận và lưu chỉ số điện nước vào CSDL thành công!", Toast.LENGTH_SHORT).show();
                        lamMoiForm();
                    } else {
                        Toast.makeText(this, "Lưu vào CSDL thất bại!", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });

        btnLamMoiDienNuoc.setOnClickListener(v -> lamMoiForm());
        btnQuayLaiDienNuoc.setOnClickListener(v -> finish());
    }

    private void loadDanhSachDienNuoc() {
        executor.execute(() -> {
            danhSachDienNuoc = db.getAllDienNuoc();
            if (danhSachDienNuoc == null) danhSachDienNuoc = new ArrayList<>();
            mainHandler.post(() -> hienThiDanhSach(danhSachDienNuoc));
        });
    }

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

    private double tinhTienDienBacThang(double soDien) {
        double thanhTien = 0;
        if (soDien <= 50) {
            thanhTien = soDien * 1800;
        } else if (soDien <= 100) {
            thanhTien = (50 * 1800) + ((soDien - 50) * 2300);
        } else {
            thanhTien = (50 * 1800) + (50 * 2300) + ((soDien - 100) * 3100);
        }
        return thanhTien;
    }

    private void tinhTongTienTamTinh() {
        double soDien = laySoDienTieuThu();
        double soNuoc = laySoNuocTieuThu();
        double tienDichVu = chuyenDinhDangSo(edtTienDichVuKhac.getText().toString().trim());
        double tienDien = tinhTienDienBacThang(soDien);

        double tongTien = tienDien + (soNuoc * donGiaNuoc) + tienDichVu;
        tvTongTienTinhToan.setText(String.format(Locale.getDefault(), "Tổng tiền tạm tính: %,.0f VNĐ", tongTien));
    }

    private void timKiemDienNuoc(String tuKhoa) {
        String tuKhoaChuan = tuKhoa.toLowerCase().trim();
        List<ModelDienNuoc> danhSachKetQua = new ArrayList<>();

        if (danhSachDienNuoc != null) {
            for (ModelDienNuoc item : danhSachDienNuoc) {
                if (item.getMaPhong().toLowerCase().contains(tuKhoaChuan) ||
                    item.getThangGhi().toLowerCase().contains(tuKhoaChuan)) {
                    danhSachKetQua.add(item);
                }
            }
        }

        dienNuocAdapter = new DienNuocAdapter(this, danhSachKetQua, null);
        recyclerDanhSachDienNuoc.setAdapter(dienNuocAdapter);
    }

    private void hienThiDialogCaiDatGia() {
        Toast.makeText(this, "Đơn giá hiện tại: Điện = " + donGiaDien + "đ/kWh | Nước = " + donGiaNuoc + "đ/m3", Toast.LENGTH_LONG).show();
    }

    private void loadDanhSachPhong() {
        executor.execute(() -> {
            List<ModelPhong> tatCaPhong = db.getAllPhong();
            List<String> tenPhongList = new ArrayList<>();
            if (tatCaPhong != null) {
                for (ModelPhong p : tatCaPhong) {
                    tenPhongList.add(p.getMaPhong() + " - " + p.getTenPhong());
                }
            }
            if (tenPhongList.isEmpty()) {
                tenPhongList.add("Không có phòng nào");
            }
            mainHandler.post(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, tenPhongList);
                dropdownChonPhongDienNuoc.setAdapter(adapter);
            });
        });
    }

    private void hienThiDanhSach(List<ModelDienNuoc> list) {
        dienNuocAdapter = new DienNuocAdapter(this, list, null);
        recyclerDanhSachDienNuoc.setLayoutManager(new LinearLayoutManager(this));
        recyclerDanhSachDienNuoc.setAdapter(dienNuocAdapter);
    }

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
