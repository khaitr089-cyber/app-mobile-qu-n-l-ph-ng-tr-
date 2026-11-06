package com.example.quanlyphongtro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
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

    TextInputEditText edtMaPhong, edtGiaThue;
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

        // Ánh xạ các view
        edtMaPhong = findViewById(R.id.edtMaPhong);
        dropdownLoaiPhong = findViewById(R.id.dropdownLoaiPhong);
        edtGiaThue = findViewById(R.id.edtGiaThue);
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
        loadDanhSachPhong();

        // 1. Thêm phòng vào SQLite
        btnThemPhong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ma = edtMaPhong.getText().toString().trim();
                String ten = dropdownLoaiPhong.getText().toString().trim();
                String gia = edtGiaThue.getText().toString().trim();
                String tt = dropdownTrangThai.getText().toString().trim();

                if (ma.isEmpty() || gia.isEmpty()) {
                    Toast.makeText(QuanLyPhong.this, "Vui lòng nhập mã phòng và giá thuê!", Toast.LENGTH_SHORT).show();
                } else {
                    boolean check = db.themPhong(ma, ten, gia, tt);
                    if (check) {
                        Toast.makeText(QuanLyPhong.this, "Thêm phòng thành công!", Toast.LENGTH_SHORT).show();
                        loadDanhSachPhong();
                        lamMoiForm();
                    } else {
                        Toast.makeText(QuanLyPhong.this, "Thêm thất bại (Mã phòng có thể đã tồn tại)!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // 2. Sửa thông tin phòng trong SQLite
        btnSuaPhong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ma = edtMaPhong.getText().toString().trim();
                String ten = dropdownLoaiPhong.getText().toString().trim();
                String gia = edtGiaThue.getText().toString().trim();
                String tt = dropdownTrangThai.getText().toString().trim();

                if (ma.isEmpty()) {
                    Toast.makeText(QuanLyPhong.this, "Vui lòng chọn phòng cần sửa từ danh sách!", Toast.LENGTH_SHORT).show();
                } else {
                    boolean check = db.suaPhong(ma, ten, gia, tt);
                    if (check) {
                        Toast.makeText(QuanLyPhong.this, "Sửa phòng thành công!", Toast.LENGTH_SHORT).show();
                        loadDanhSachPhong();
                        lamMoiForm();
                    } else {
                        Toast.makeText(QuanLyPhong.this, "Sửa thất bại!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // 3. Xóa phòng khỏi SQLite
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
                        loadDanhSachPhong();
                        lamMoiForm();
                    } else {
                        Toast.makeText(QuanLyPhong.this, "Xóa thất bại!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // 4. Làm mới form
        btnLamMoiPhong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lamMoiForm();
                Toast.makeText(QuanLyPhong.this, "Đã làm mới form", Toast.LENGTH_SHORT).show();
            }
        });

        // 5. Quay lại
        btnQuayLaiPhong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadDanhSachPhong() {
        listPhong = db.getAllPhong();
        phongAdapter = new PhongAdapter(this, listPhong, new PhongAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ModelPhong phong) {
                edtMaPhong.setText(phong.getMaPhong());
                edtMaPhong.setEnabled(false);
                dropdownLoaiPhong.setText(phong.getTenPhong(), false);
                edtGiaThue.setText(phong.getGiaThue());
                dropdownTrangThai.setText(phong.getTrangThai(), false);
            }
        });
        recyclerDanhSachPhong.setLayoutManager(new LinearLayoutManager(this));
        recyclerDanhSachPhong.setAdapter(phongAdapter);
    }

    private void lamMoiForm() {
        edtMaPhong.setText("");
        edtMaPhong.setEnabled(true);
        dropdownLoaiPhong.setText("Phòng đơn", false);
        edtGiaThue.setText("");
        dropdownTrangThai.setText("Còn trống", false);
        edtMaPhong.requestFocus();
    }
}
