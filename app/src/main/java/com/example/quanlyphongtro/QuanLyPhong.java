package com.example.quanlyphongtro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import java.util.List;

import com.example.quanlyphongtro.adapter.PhongAdapter;
import com.example.quanlyphongtro.model.ModelPhong;

public class QuanLyPhong extends AppCompatActivity {

    // Khai báo các view (Đã bỏ edtSoNguoiToiDa)
    TextInputEditText edtMaPhong, edtGiaThue, edtTang, edtDienTich, edtTimKiemPhong;
    AutoCompleteTextView dropdownLoaiPhong, dropdownTrangThai;
    Button btnThemPhong, btnSuaPhong, btnXoaPhong, btnLamMoiPhong, btnQuayLaiPhong;
    RecyclerView recyclerDanhSachPhong;

    QuanLyCoSoDuLieu db;
    List<ModelPhong> listPhong;
    PhongAdapter phongAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giaodienphongtro);

        db = new QuanLyCoSoDuLieu(this);

        // --- BƯỚC 1: ÁNH XẠ CÁC VIEW TỪ XML SANG JAVA ---
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

        // Thiết lập dữ liệu cho Dropdown Loại Phòng
        String[] loaiPhongArray = new String[]{"Phòng đơn", "Phòng đôi", "Studio"};
        ArrayAdapter<String> adapterLoaiPhong = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, loaiPhongArray);
        dropdownLoaiPhong.setAdapter(adapterLoaiPhong);

        // Thiết lập dữ liệu cho Dropdown Trạng Thái
        String[] trangThaiArray = new String[]{"Còn trống", "Đang thuê"};
        ArrayAdapter<String> adapterTrangThai = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, trangThaiArray);
        dropdownTrangThai.setAdapter(adapterTrangThai);

        // Load danh sách phòng từ SQLite
        loadDanhSachPhong("");

        // --- BƯỚC 2: TÍNH NĂNG TÌM KIẾM PHÒNG (F05) ---
        edtTimKiemPhong.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = s.toString().trim();
                loadDanhSachPhong(keyword);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // --- BƯỚC 3: THÊM PHÒNG VÀO SQLITE ---
        btnThemPhong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ma = edtMaPhong.getText().toString().trim();
                String ten = dropdownLoaiPhong.getText().toString().trim();
                String gia = edtGiaThue.getText().toString().trim();
                String tt = dropdownTrangThai.getText().toString().trim();
                String tang = edtTang.getText().toString().trim();
                String dienTich = edtDienTich.getText().toString().trim();

                if (ma.isEmpty() || gia.isEmpty()) {
                    Toast.makeText(QuanLyPhong.this, "Vui lòng nhập mã phòng và giá thuê!", Toast.LENGTH_SHORT).show();
                } else {
                    boolean check = db.themPhong(ma, ten, gia, tt, tang, dienTich);
                    if (check) {
                        Toast.makeText(QuanLyPhong.this, "Thêm phòng thành công!", Toast.LENGTH_SHORT).show();
                        loadDanhSachPhong("");
                        lamMoiForm();
                    } else {
                        Toast.makeText(QuanLyPhong.this, "Thêm thất bại (Mã phòng có thể đã tồn tại)!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // --- BƯỚC 4: SỬA THÔNG TIN PHÒNG TRONG SQLITE ---
        btnSuaPhong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ma = edtMaPhong.getText().toString().trim();
                String ten = dropdownLoaiPhong.getText().toString().trim();
                String gia = edtGiaThue.getText().toString().trim();
                String tt = dropdownTrangThai.getText().toString().trim();
                String tang = edtTang.getText().toString().trim();
                String dienTich = edtDienTich.getText().toString().trim();

                if (ma.isEmpty()) {
                    Toast.makeText(QuanLyPhong.this, "Vui lòng chọn phòng cần sửa từ danh sách!", Toast.LENGTH_SHORT).show();
                } else {
                    boolean check = db.suaPhong(ma, ten, gia, tt, tang, dienTich);
                    if (check) {
                        Toast.makeText(QuanLyPhong.this, "Sửa phòng thành công!", Toast.LENGTH_SHORT).show();
                        loadDanhSachPhong("");
                        lamMoiForm();
                    } else {
                        Toast.makeText(QuanLyPhong.this, "Sửa thất bại!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // --- BƯỚC 5: XÓA PHÒNG KHỎI SQLITE ---
        btnXoaPhong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ma = edtMaPhong.getText().toString().trim();
                if (ma.isEmpty()) {
                    Toast.makeText(QuanLyPhong.this, "Vui lòng chọn phòng cần xóa từ danh sách!", Toast.LENGTH_SHORT).show();
                } else {
                    boolean check = db.xoaPhong(ma);
                    if (check) {
                        Toast.makeText(QuanLyPhong.this, "Xóa phòng thành công!", Toast.LENGTH_SHORT).show();
                        loadDanhSachPhong("");
                        lamMoiForm();
                    } else {
                        Toast.makeText(QuanLyPhong.this, "Xóa thất bại!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // --- BƯỚC 6: LÀM MỚI FORM ---
        btnLamMoiPhong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lamMoiForm();
                Toast.makeText(QuanLyPhong.this, "Đã làm mới form", Toast.LENGTH_SHORT).show();
            }
        });

        // --- BƯỚC 7: QUAY LẠI MÀN HÌNH CHÍNH ---
        btnQuayLaiPhong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // --- HÀM TẢI VÀ TÌM KIẾM DANH SÁCH PHÒNG ---
    private void loadDanhSachPhong(String keyword) {
        if (keyword.isEmpty()) {
            listPhong = db.getAllPhong();
        } else {
            listPhong = db.searchPhong(keyword);
        }

        phongAdapter = new PhongAdapter(this, listPhong, new PhongAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ModelPhong phong) {
                edtMaPhong.setText(phong.getMaPhong());
                edtMaPhong.setEnabled(false); // Khóa không cho sửa mã phòng
                dropdownLoaiPhong.setText(phong.getTenPhong(), false);
                edtGiaThue.setText(phong.getGiaThue());
                edtTang.setText(phong.getTang());
                edtDienTich.setText(phong.getDienTich());
                dropdownTrangThai.setText(phong.getTrangThai(), false);
            }
        });
        recyclerDanhSachPhong.setLayoutManager(new LinearLayoutManager(this));
        recyclerDanhSachPhong.setAdapter(phongAdapter);
    }

    // --- HÀM LÀM TRẮNG FORM ---
    private void lamMoiForm() {
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
