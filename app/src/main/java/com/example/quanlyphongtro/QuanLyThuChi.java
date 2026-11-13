package com.example.quanlyphongtro;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class QuanLyThuChi extends AppCompatActivity {

    TextInputEditText txtDanhMuc, txtSoTien, txtGhiChuThuChi;
    Button btnGhiChi, btnGhiThu, btnQuayLaiThuChi;
    TextView txtTongChiPhi;

    QuanLyCoSoDuLieu dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giaodienthuchi);

        dbHelper = new QuanLyCoSoDuLieu(this);

        // Ánh xạ
        txtDanhMuc = findViewById(R.id.txtdanhmuc);
        txtSoTien = findViewById(R.id.txtsotien);
        txtGhiChuThuChi = findViewById(R.id.txtghichuthuchi);
        
        btnGhiChi = findViewById(R.id.btnGhiChi);
        btnGhiThu = findViewById(R.id.btnGhiThu);
        btnQuayLaiThuChi = findViewById(R.id.btnQuayLaiThuChi);
        
        txtTongChiPhi = findViewById(R.id.txtTongChiPhi);

        // Load tổng chi phí
        capNhatTongChiPhi();

        // Sự kiện Ghi khoản chi
        btnGhiChi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ghiNhanThuChi("CHI");
            }
        });

        // Sự kiện Ghi khoản thu
        btnGhiThu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ghiNhanThuChi("THU");
            }
        });

        btnQuayLaiThuChi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void ghiNhanThuChi(String loai) {
        String danhMuc = txtDanhMuc.getText().toString().trim();
        String soTienStr = txtSoTien.getText().toString().trim();
        String ghiChu = txtGhiChuThuChi.getText().toString().trim();

        if (danhMuc.isEmpty() || soTienStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập danh mục và số tiền!", Toast.LENGTH_SHORT).show();
            return;
        }

        double soTien = Double.parseDouble(soTienStr);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String ngayGhi = sdf.format(new Date());

        boolean check = dbHelper.themThuChi(loai, soTien, danhMuc, ghiChu, ngayGhi);
        if (check) {
            Toast.makeText(this, "Đã ghi nhận khoản " + (loai.equals("CHI") ? "chi phí" : "thu nhập") + " thành công!", Toast.LENGTH_SHORT).show();
            txtDanhMuc.setText("");
            txtSoTien.setText("");
            txtGhiChuThuChi.setText("");
            capNhatTongChiPhi();
        } else {
            Toast.makeText(this, "Ghi nhận thất bại!", Toast.LENGTH_SHORT).show();
        }
    }

    private void capNhatTongChiPhi() {
        double tongChi = dbHelper.getTongChiPhi();
        txtTongChiPhi.setText(String.format(Locale.getDefault(), "%,.0f VNĐ", tongChi));
    }
}
