package com.example.quanlyphongtro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ManHinhDangNhap extends AppCompatActivity {

    EditText txttaikhoan, txtmatkhau;
    Button btndangnhap, btndangki;
    QuanLyCoSoDuLieu db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giaodiendangnhap);

        txttaikhoan = findViewById(R.id.txttaikhoan);
        txtmatkhau = findViewById(R.id.txtmatkhau);
        btndangnhap = findViewById(R.id.btndangnhap);
        btndangki = findViewById(R.id.btndangki);

        db = new QuanLyCoSoDuLieu(this);

        btndangnhap.setOnClickListener(v -> {
            String taiKhoan = txttaikhoan.getText().toString().trim();
            String matKhau = txtmatkhau.getText().toString().trim();

            if (db.dangnhap(taiKhoan, matKhau)) {
                // Lưu tên tài khoản hiện tại vào Session và SharedPreferences để giữ phiên làm việc
                UserSession.saveUser(this, taiKhoan);

                Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ManHinhDangNhap.this, ManHinhChinh.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show();
            }
        });

        btndangki.setOnClickListener(v -> {
            Intent intent = new Intent(ManHinhDangNhap.this, ManHinhDangKy.class);
            startActivity(intent);
        });

    }
}
