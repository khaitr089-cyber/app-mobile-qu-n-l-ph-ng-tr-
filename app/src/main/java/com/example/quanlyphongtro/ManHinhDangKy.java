package com.example.quanlyphongtro;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ManHinhDangKy extends AppCompatActivity {

    EditText txttaikhoan, txtmatkhau, txthotenchunha, txtsdtchunha, txtcccdchunha;
    Button btndangki, btnquaylai;
    QuanLyCoSoDuLieu db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giaodiendangki);

        txttaikhoan = findViewById(R.id.txttaikhoan);
        txtmatkhau = findViewById(R.id.txtmatkhau);
        txthotenchunha = findViewById(R.id.txthotenchunha);
        txtsdtchunha = findViewById(R.id.txtsdtchunha);
        txtcccdchunha = findViewById(R.id.txtcccdchunha);

        btndangki = findViewById(R.id.btndangki);
        btnquaylai = findViewById(R.id.btnquaylai);

        db = new QuanLyCoSoDuLieu(this);

        btndangki.setOnClickListener(v -> {
            String taiKhoan = txttaikhoan.getText().toString().trim();
            String matKhau = txtmatkhau.getText().toString().trim();
            String hoTen = txthotenchunha.getText().toString().trim();
            String sdt = txtsdtchunha.getText().toString().trim();
            String cccd = txtcccdchunha.getText().toString().trim();

            if (taiKhoan.isEmpty() || matKhau.isEmpty() || hoTen.isEmpty() || sdt.isEmpty() || cccd.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin Chủ nhà!", Toast.LENGTH_SHORT).show();
            } else {
                boolean success = db.dangkyFull(taiKhoan, matKhau, hoTen, sdt, cccd);
                if (success) {
                    Toast.makeText(this, "Đăng ký thành công thông tin Chủ trọ!", Toast.LENGTH_SHORT).show();
                    finish(); // Quay lại màn hình đăng nhập
                } else {
                    Toast.makeText(this, "Tài khoản đã tồn tại hoặc lỗi đăng ký", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnquaylai.setOnClickListener(v -> finish());
    }
}
