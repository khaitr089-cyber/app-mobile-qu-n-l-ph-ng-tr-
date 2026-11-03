package com.example.quanlyphongtro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import java.util.List;

public class QuanLyPhong extends AppCompatActivity {

    TextInputEditText edtMaPhong, edtTenPhong, edtGiaThue, edtTrangThai;
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
        edtTenPhong = findViewById(R.id.edtTenPhong);
        edtGiaThue = findViewById(R.id.edtGiaThue);
        edtTrangThai = findViewById(R.id.edtTrangThai);

        btnThemPhong = findViewById(R.id.btnThemPhong);
        btnSuaPhong = findViewById(R.id.btnSuaPhong);
        btnXoaPhong = findViewById(R.id.btnXoaPhong);
        btnLamMoiPhong = findViewById(R.id.btnLamMoiPhong);
        btnQuayLaiPhong = findViewById(R.id.btnQuayLaiPhong);
        recyclerDanhSachPhong = findViewById(R.id.recyclerDanhSachPhong);

        // Load danh sách phòng từ SQLite
        loadDanhSachPhong();

        // 1. Thêm phòng vào SQLite
        btnThemPhong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ma = edtMaPhong.getText().toString().trim();
                String ten = edtTenPhong.getText().toString().trim();
                String gia = edtGiaThue.getText().toString().trim();
                String tt = edtTrangThai.getText().toString().trim();

                if (ma.isEmpty() || ten.isEmpty() || gia.isEmpty() || tt.isEmpty()) {
                    Toast.makeText(QuanLyPhong.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
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
                String ten = edtTenPhong.getText().toString().trim();
                String gia = edtGiaThue.getText().toString().trim();
                String tt = edtTrangThai.getText().toString().trim();

                if (ma.isEmpty()) {
                    Toast.makeText(QuanLyPhong.this, "Vui lòng chọn hoặc nhập mã phòng cần sửa!", Toast.LENGTH_SHORT).show();
                } else {
                    boolean check = db.suaPhong(ma, ten, gia, tt);
                    if (check) {
                        Toast.makeText(QuanLyPhong.this, "Sửa phòng thành công!", Toast.LENGTH_SHORT).show();
                        loadDanhSachPhong();
                        lamMoiForm();
                    } else {
                        Toast.makeText(QuanLyPhong.this, "Sửa thất bại (Không tìm thấy mã phòng)!", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(QuanLyPhong.this, "Vui lòng nhập hoặc chọn mã phòng cần xóa!", Toast.LENGTH_SHORT).show();
                } else {
                    boolean check = db.xoaPhong(ma);
                    if (check) {
                        Toast.makeText(QuanLyPhong.this, "Xóa phòng thành công!", Toast.LENGTH_SHORT).show();
                        loadDanhSachPhong();
                        lamMoiForm();
                    } else {
                        Toast.makeText(QuanLyPhong.this, "Xóa thất bại (Không tìm thấy mã phòng)!", Toast.LENGTH_SHORT).show();
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

    // Hàm load danh sách phòng từ SQLite lên RecyclerView
    private void loadDanhSachPhong() {
        listPhong = db.getAllPhong();
        phongAdapter = new PhongAdapter(this, listPhong, new PhongAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ModelPhong phong) {
                // Khi click vào 1 item trên danh sách, đổ dữ liệu lên form để sửa/xóa
                edtMaPhong.setText(phong.getMaPhong());
                edtMaPhong.setEnabled(false); // Không cho sửa mã phòng khi đang chỉnh sửa
                edtTenPhong.setText(phong.getTenPhong());
                edtGiaThue.setText(phong.getGiaThue());
                edtTrangThai.setText(phong.getTrangThai());
            }
        });
        recyclerDanhSachPhong.setLayoutManager(new LinearLayoutManager(this));
        recyclerDanhSachPhong.setAdapter(phongAdapter);
    }

    // Hàm làm mới form nhập liệu
    private void lamMoiForm() {
        edtMaPhong.setText("");
        edtMaPhong.setEnabled(true); // Cho phép nhập lại mã phòng khi thêm mới
        edtTenPhong.setText("");
        edtGiaThue.setText("");
        edtTrangThai.setText("");
        edtMaPhong.requestFocus();
    }
}
