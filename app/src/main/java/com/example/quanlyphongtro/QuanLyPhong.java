package com.example.quanlyphongtro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.quanlyphongtro.adapter.PhongAdapter;
import com.example.quanlyphongtro.model.ModelPhong;

public class QuanLyPhong extends AppCompatActivity {

    TextInputEditText edtMaPhong, edtGiaThue, edtTang, edtDienTich, edtTimKiemPhong;
    AutoCompleteTextView dropdownLoaiPhong, dropdownTrangThai;
    Button btnThemPhong, btnSuaPhong, btnXoaPhong, btnLamMoiPhong, btnQuayLaiPhong;
    RecyclerView recyclerDanhSachPhong;

    QuanLyCoSoDuLieu db;
    List<ModelPhong> listPhong;
    PhongAdapter phongAdapter;
    private String selectedMaPhong = "";

    // Luồng xử lý bất đồng bộ (Executor) và Handler giao diện
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giaodienphongtro);

        db = new QuanLyCoSoDuLieu(this);

        // --- 1. ÁNH XẠ CÁC VIEW ---
        edtMaPhong = findViewById(R.id.edtMaPhong);
        dropdownLoaiPhong = findViewById(R.id.dropdownLoaiPhong);
        edtGiaThue = findViewById(R.id.edtGiaThue);
        edtTang = findViewById(R.id.edtTang);
        edtDienTich = findViewById(R.id.edtDienTich);
        edtTimKiemPhong = findViewById(R.id.edtTimKiemPhong);
        dropdownTrangThai = findViewById(R.id.dropdownTrangThai);

        btnThemPhong = findViewById(R.id.btnThemPhong);
        btnSuaPhong = findViewById(R.id.btnSuaPhong);
        btnXoaPhong = findViewById(R.id.btnXoaPhong);
        btnLamMoiPhong = findViewById(R.id.btnLamMoiPhong);
        btnQuayLaiPhong = findViewById(R.id.btnQuayLaiPhong);
        recyclerDanhSachPhong = findViewById(R.id.recyclerDanhSachPhong);

        // Thiết lập Dropdown Loại Phòng
        String[] loaiPhongArray = new String[]{"Phòng đơn", "Phòng đôi", "Studio"};
        ArrayAdapter<String> adapterLoaiPhong = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, loaiPhongArray);
        dropdownLoaiPhong.setAdapter(adapterLoaiPhong);

        // Thiết lập Dropdown Trạng Thái
        String[] trangThaiArray = new String[]{"Còn trống", "Đang thuê"};
        ArrayAdapter<String> adapterTrangThai = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, trangThaiArray);
        dropdownTrangThai.setAdapter(adapterTrangThai);

        // Tải danh sách phòng
        loadDanhSachPhong("");

        // Nút lọc phòng nhanh
        Button btnLocTatCa = findViewById(R.id.btnLocTatCa);
        Button btnLocPhongTrong = findViewById(R.id.btnLocPhongTrong);
        Button btnLocDangThue = findViewById(R.id.btnLocDangThue);

        if (btnLocTatCa != null) btnLocTatCa.setOnClickListener(v -> loadDanhSachPhong(""));
        if (btnLocPhongTrong != null) btnLocPhongTrong.setOnClickListener(v -> loadPhongTheoTrangThai("trống"));
        if (btnLocDangThue != null) btnLocDangThue.setOnClickListener(v -> loadPhongTheoTrangThai("đang thuê"));

        // --- 2. THUẬT TOÁN DEBOUNCE 250MS TÌM KIẾM TỐI ƯU ---
        Handler debounceHandler = new Handler(Looper.getMainLooper());
        Runnable searchRunnable = () -> {
            String keyword = edtTimKiemPhong.getText() != null ? edtTimKiemPhong.getText().toString().trim() : "";
            loadDanhSachPhong(keyword);
        };

        edtTimKiemPhong.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                debounceHandler.removeCallbacks(searchRunnable);
                debounceHandler.postDelayed(searchRunnable, 250); // Trì hoãn 250ms tránh giật lag khi gõ
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // --- 3. THÊM PHÒNG ---
        btnThemPhong.setOnClickListener(v -> {
            String ma = edtMaPhong.getText() != null ? edtMaPhong.getText().toString().trim() : "";
            String ten = dropdownLoaiPhong.getText().toString().trim();
            String gia = edtGiaThue.getText() != null ? edtGiaThue.getText().toString().trim() : "";
            String tt = dropdownTrangThai.getText().toString().trim();
            String tang = edtTang.getText() != null ? edtTang.getText().toString().trim() : "";
            String dienTich = edtDienTich.getText() != null ? edtDienTich.getText().toString().trim() : "";

            if (ma.isEmpty() || gia.isEmpty()) {
                Toast.makeText(QuanLyPhong.this, "Vui lòng nhập mã phòng và giá thuê!", Toast.LENGTH_SHORT).show();
            } else {
                executor.execute(() -> {
                    boolean check = db.themPhong(ma, ten, gia, tt, tang, dienTich);
                    mainHandler.post(() -> {
                        if (check) {
                            Toast.makeText(QuanLyPhong.this, "Thêm phòng thành công!", Toast.LENGTH_SHORT).show();
                            loadDanhSachPhong("");
                            lamMoiForm();
                        } else {
                            Toast.makeText(QuanLyPhong.this, "Thêm thất bại (Mã phòng đã tồn tại)!", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            }
        });

        // --- 4. SỬA PHÒNG ---
        btnSuaPhong.setOnClickListener(v -> {
            if (selectedMaPhong.isEmpty()) {
                Toast.makeText(QuanLyPhong.this, "Vui lòng chọn 1 phòng trong danh sách để sửa!", Toast.LENGTH_SHORT).show();
                return;
            }
            String ma = selectedMaPhong;
            String ten = dropdownLoaiPhong.getText().toString().trim();
            String gia = edtGiaThue.getText() != null ? edtGiaThue.getText().toString().trim() : "";
            String tt = dropdownTrangThai.getText().toString().trim();
            String tang = edtTang.getText() != null ? edtTang.getText().toString().trim() : "";
            String dienTich = edtDienTich.getText() != null ? edtDienTich.getText().toString().trim() : "";

            executor.execute(() -> {
                boolean check = db.suaPhong(ma, ten, gia, tt, tang, dienTich);
                mainHandler.post(() -> {
                    if (check) {
                        Toast.makeText(QuanLyPhong.this, "Cập nhật thông tin phòng thành công!", Toast.LENGTH_SHORT).show();
                        loadDanhSachPhong("");
                        lamMoiForm();
                    } else {
                        Toast.makeText(QuanLyPhong.this, "Sửa thất bại!", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });

        // --- 5. XÓA PHÒNG ---
        btnXoaPhong.setOnClickListener(v -> {
            if (selectedMaPhong.isEmpty()) {
                Toast.makeText(QuanLyPhong.this, "Vui lòng chọn 1 phòng trong danh sách để xóa!", Toast.LENGTH_SHORT).show();
                return;
            }
            executor.execute(() -> {
                boolean check = db.xoaPhong(selectedMaPhong);
                mainHandler.post(() -> {
                    if (check) {
                        Toast.makeText(QuanLyPhong.this, "Đã xóa phòng thành công!", Toast.LENGTH_SHORT).show();
                        loadDanhSachPhong("");
                        lamMoiForm();
                    } else {
                        Toast.makeText(QuanLyPhong.this, "Xóa thất bại!", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });

        // --- 6. LÀM MỚI FORM & QUAY LẠI ---
        btnLamMoiPhong.setOnClickListener(v -> lamMoiForm());
        btnQuayLaiPhong.setOnClickListener(v -> finish());
    }

    // --- HÀM TẢI LỌC THEO TRẠNG THÁI (BẤT ĐỒNG BỘ) ---
    private void loadPhongTheoTrangThai(String tt) {
        executor.execute(() -> {
            List<ModelPhong> resultList = db.getPhongByTrangThai(tt);
            if (resultList == null) resultList = new ArrayList<>();
            List<ModelPhong> finalResultList = resultList;

            mainHandler.post(() -> {
                listPhong = finalResultList;
                phongAdapter = new PhongAdapter(this, listPhong, phong -> {
                    selectedMaPhong = phong.getMaPhong();
                    edtMaPhong.setText(phong.getMaPhong());
                    edtMaPhong.setEnabled(false);
                    dropdownLoaiPhong.setText(phong.getTenPhong(), false);
                    edtGiaThue.setText(phong.getGiaThue());
                    edtTang.setText(phong.getTang());
                    edtDienTich.setText(phong.getDienTich());
                    dropdownTrangThai.setText(phong.getTrangThai(), false);
                });
                recyclerDanhSachPhong.setLayoutManager(new LinearLayoutManager(this));
                recyclerDanhSachPhong.setAdapter(phongAdapter);
            });
        });
    }

    // --- HÀM TẢI VÀ TÌM KIẾM DANH SÁCH PHÒNG (BẤT ĐỒNG BỘ EXECUTOR) ---
    private void loadDanhSachPhong(String keyword) {
        executor.execute(() -> {
            List<ModelPhong> resultList;
            if (keyword.isEmpty()) {
                resultList = db.getAllPhong();
            } else {
                resultList = db.searchPhong(keyword);
            }
            if (resultList == null) resultList = new ArrayList<>();
            List<ModelPhong> finalResultList = resultList;

            mainHandler.post(() -> {
                listPhong = finalResultList;
                phongAdapter = new PhongAdapter(this, listPhong, phong -> {
                    selectedMaPhong = phong.getMaPhong();
                    edtMaPhong.setText(phong.getMaPhong());
                    edtMaPhong.setEnabled(false);
                    dropdownLoaiPhong.setText(phong.getTenPhong(), false);
                    edtGiaThue.setText(phong.getGiaThue());
                    edtTang.setText(phong.getTang());
                    edtDienTich.setText(phong.getDienTich());
                    dropdownTrangThai.setText(phong.getTrangThai(), false);
                });
                recyclerDanhSachPhong.setLayoutManager(new LinearLayoutManager(this));
                recyclerDanhSachPhong.setAdapter(phongAdapter);
            });
        });
    }

    private void lamMoiForm() {
        selectedMaPhong = "";
        edtMaPhong.setText("");
        edtMaPhong.setEnabled(true);
        dropdownLoaiPhong.setText("Phòng đơn", false);
        edtGiaThue.setText("");
        edtTang.setText("");
        edtDienTich.setText("");
        dropdownTrangThai.setText("Còn trống", false);
        edtTimKiemPhong.setText("");
        edtMaPhong.requestFocus();
    }
}
