package com.example.quanlyphongtro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;

public class QuanLyNguoiThue extends AppCompatActivity {

    // Khai báo biến các thành phần giao diện người thuê
    TextInputEditText edtHoTen, edtSoDienThoai, edtCCCD, edtPhongThue, edtQueQuan;
    Button btnThemNguoiThue, btnSuaNguoiThue, btnXoaNguoiThue, btnLamMoiNguoiThue, btnQuayLaiNguoiThue;
    RecyclerView recyclerDanhSachNguoiThue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giaodiennguoithue);

        // Ánh xạ các view từ XML sang Java
        edtHoTen = findViewById(R.id.edtHoTen);
        edtSoDienThoai = findViewById(R.id.edtSoDienThoai);
        edtCCCD = findViewById(R.id.edtCCCD);
        edtPhongThue = findViewById(R.id.edtPhongThue);
        edtQueQuan = findViewById(R.id.edtQueQuan);

        btnThemNguoiThue = findViewById(R.id.btnThemNguoiThue);
        btnSuaNguoiThue = findViewById(R.id.btnSuaNguoiThue);
        btnXoaNguoiThue = findViewById(R.id.btnXoaNguoiThue);
        btnLamMoiNguoiThue = findViewById(R.id.btnLamMoiNguoiThue);
        btnQuayLaiNguoiThue = findViewById(R.id.btnQuayLaiNguoiThue);

        recyclerDanhSachNguoiThue = findViewById(R.id.recyclerDanhSachNguoiThue);

        // 1. Chức năng Thêm người thuê
        btnThemNguoiThue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hoTen = edtHoTen.getText().toString().trim();
                String sdt = edtSoDienThoai.getText().toString().trim();
                String cccd = edtCCCD.getText().toString().trim();
                String phongThue = edtPhongThue.getText().toString().trim();
                String queQuan = edtQueQuan.getText().toString().trim();

                // Kiểm tra xem người dùng đã nhập đủ thông tin chưa
                if (hoTen.isEmpty() || sdt.isEmpty() || cccd.isEmpty() || phongThue.isEmpty() || queQuan.isEmpty()) {
                    Toast.makeText(QuanLyNguoiThue.this, "Vui lòng nhập đầy đủ thông tin người thuê!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(QuanLyNguoiThue.this, "Thêm người thuê thành công: " + hoTen, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 2. Chức năng Sửa người thuê
        btnSuaNguoiThue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cccd = edtCCCD.getText().toString().trim();
                if (cccd.isEmpty()) {
                    Toast.makeText(QuanLyNguoiThue.this, "Vui lòng nhập số CCCD của người cần sửa!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(QuanLyNguoiThue.this, "Sửa thông tin người thuê có CCCD: " + cccd + " thành công!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 3. Chức năng Xóa người thuê
        btnXoaNguoiThue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cccd = edtCCCD.getText().toString().trim();
                if (cccd.isEmpty()) {
                    Toast.makeText(QuanLyNguoiThue.this, "Vui lòng nhập số CCCD của người cần xóa!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(QuanLyNguoiThue.this, "Xóa người thuê có CCCD: " + cccd + " thành công!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 4. Chức năng Làm mới (Xóa trắng các ô nhập liệu)
        btnLamMoiNguoiThue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtHoTen.setText("");
                edtSoDienThoai.setText("");
                edtCCCD.setText("");
                edtPhongThue.setText("");
                edtQueQuan.setText("");
                edtHoTen.requestFocus();
                Toast.makeText(QuanLyNguoiThue.this, "Đã làm mới ô nhập liệu", Toast.LENGTH_SHORT).show();
            }
        });

        // 5. Chức năng Quay lại
        btnQuayLaiNguoiThue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Đóng Activity hiện tại để quay về màn hình trước
            }
        });
    }
}
