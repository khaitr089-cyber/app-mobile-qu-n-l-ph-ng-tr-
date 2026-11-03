package com.example.quanlyphongtro;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ManHinhDangKy extends AppCompatActivity {

    EditText txttaikhoan, txtmatkhau, txtxacnhanmatkhau;
    Button btndangki, btnquaylai;
    QuanLyCoSoDuLieu db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giaodiendangki);

        txttaikhoan = findViewById(R.id.txttaikhoan);
        txtmatkhau = findViewById(R.id.txtmatkhau);
        txtxacnhanmatkhau = findViewById(R.id.txtxacnhanmatkhau);
        btndangki = findViewById(R.id.btndangki);
        btnquaylai = findViewById(R.id.btnquaylai);

        db = new QuanLyCoSoDuLieu(this);

        btndangki.setOnClickListener(v -> {
            String taiKhoan = txttaikhoan.getText().toString().trim();
            String matKhau = txtmatkhau.getText().toString().trim();
            String xacNhan = txtxacnhanmatkhau.getText().toString().trim();

            if (taiKhoan.isEmpty() || matKhau.isEmpty() || xacNhan.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            } else if (!matKhau.equals(xacNhan)) {
                Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
            } else {
                boolean success = db.dangky(taiKhoan, matKhau);
                if (success) {
                    Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    finish(); // Quay lại màn hình đăng nhập
                } else {
                    Toast.makeText(this, "Tài khoản đã tồn tại hoặc lỗi đăng ký", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnquaylai.setOnClickListener(v -> finish());
    }
}
