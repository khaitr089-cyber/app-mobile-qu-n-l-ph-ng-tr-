package com.example.quanlyphongtro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;

public class QuanLyPhong extends AppCompatActivity {

    // Khai báo biến các thành phần giao diện
    TextInputEditText edtMaPhong, edtTenPhong, edtGiaThue, edtTrangThai;
    Button btnThemPhong, btnSuaPhong, btnXoaPhong, btnLamMoiPhong, btnQuayLaiPhong;
    RecyclerView recyclerDanhSachPhong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giaodienphongtro);

        // Ánh xạ các view từ XML sang Java
        edtMaPhong = findViewById(R.id.edtMaPhong);
        edtTenPhong = findViewById(R.id.edtTenPhong);
        edtGiaThue = findViewById(R.id.edtGiaThue);
        edtTrangThai = findViewById(R.id.edtTrangThai);

        btnThemPhong = findViewById(R.id.btnThemPhong);
        btnSuaPhong = findViewById(R.id.btnSuaPhong);
        btnXoaPhong = findViewById(R.id.btnXoaPhong);
        btnLamMoiPhong = findViewById(R.id.btnLamMoiPhong);
        btnQuayLaiPhong = findViewById(R.id.btnQuayLaiPhong);

        recyclerDanhSachPhong = findViewById(R.id.recyclerDanhSachPhong);

        // 1. Chức năng Thêm phòng
        btnThemPhong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String maPhong = edtMaPhong.getText().toString().trim();
                String tenPhong = edtTenPhong.getText().toString().trim();
                String giaThue = edtGiaThue.getText().toString().trim();
                String trangThai = edtTrangThai.getText().toString().trim();

                // Kiểm tra xem người dùng đã nhập đủ thông tin chưa
                if (maPhong.isEmpty() || tenPhong.isEmpty() || giaThue.isEmpty() || trangThai.isEmpty()) {
                    Toast.makeText(QuanLyPhong.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(QuanLyPhong.this, "Thêm phòng thành công: " + tenPhong, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 2. Chức năng Sửa phòng
        btnSuaPhong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String maPhong = edtMaPhong.getText().toString().trim();
                if (maPhong.isEmpty()) {
                    Toast.makeText(QuanLyPhong.this, "Vui lòng nhập mã phòng cần sửa!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(QuanLyPhong.this, "Sửa thông tin phòng " + maPhong + " thành công!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 3. Chức năng Xóa phòng
        btnXoaPhong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String maPhong = edtMaPhong.getText().toString().trim();
                if (maPhong.isEmpty()) {
                    Toast.makeText(QuanLyPhong.this, "Vui lòng nhập mã phòng cần xóa!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(QuanLyPhong.this, "Xóa phòng " + maPhong + " thành công!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 4. Chức năng Làm mới (Xóa trắng các ô nhập liệu)
        btnLamMoiPhong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtMaPhong.setText("");
            
                edtTenPhong.setText("");
                edtGiaThue.setText("");
                edtTrangThai.setText("");
                edtMaPhong.requestFocus();
                Toast.makeText(QuanLyPhong.this, "Đã làm mới ô nhập liệu", Toast.LENGTH_SHORT).show();
            }
        });

        // 5. Chức năng Quay lại
        btnQuayLaiPhong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
