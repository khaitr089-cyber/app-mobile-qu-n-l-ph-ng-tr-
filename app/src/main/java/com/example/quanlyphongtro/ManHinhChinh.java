package com.example.quanlyphongtro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class ManHinhChinh extends AppCompatActivity {
    // code chuyển trang

    // Khai báo các view
    CardView cvquanlyphongtro, cvquanlynguoithue, cvquanlyhoadon, cvthongke;
    ImageButton btnImgQuanLyPhong, btnImgNguoiThue, btnImgHoaDon, btnImgThongKe;
    Button btndangxuat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giaodientrangchu);

        // Ánh xạ các CardView
        cvquanlyphongtro = findViewById(R.id.cvquanlyphongtro);
        cvquanlynguoithue = findViewById(R.id.cvquanlynguoithue);
        cvquanlyhoadon = findViewById(R.id.cvquanlyhoadon);
        cvthongke = findViewById(R.id.cvthongke);

        // Ánh xạ các ImageButton
        btnImgQuanLyPhong = findViewById(R.id.btnimgquanlyphongtro);
        btnImgNguoiThue = findViewById(R.id.btnimgnguoithue);
        btnImgHoaDon = findViewById(R.id.btnimghoadon);
        btnImgThongKe = findViewById(R.id.btnimgthongke);
        
        // Ánh xạ nút Đăng xuất
        btndangxuat = findViewById(R.id.btndangxuat);

        //-------------------------------------------------------------------------------------

        View.OnClickListener listenerQuanLyPhong = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManHinhChinh.this, QuanLyPhong.class);
                startActivity(intent);
            }
        };
        if (cvquanlyphongtro != null) cvquanlyphongtro.setOnClickListener(listenerQuanLyPhong);
        if (btnImgQuanLyPhong != null) btnImgQuanLyPhong.setOnClickListener(listenerQuanLyPhong);

        //-------------------------------------------------------------------------------------
        // 2. Sự kiện chuyển trang: Quản lý người thuê
        View.OnClickListener listenerNguoiThue = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManHinhChinh.this, QuanLyNguoiThue.class);
                startActivity(intent);
            }
        };
        if (cvquanlynguoithue != null) cvquanlynguoithue.setOnClickListener(listenerNguoiThue);
        if (btnImgNguoiThue != null) btnImgNguoiThue.setOnClickListener(listenerNguoiThue);

        //--------------------------------------------------------------------------------------

        View.OnClickListener listenerHoaDon = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManHinhChinh.this, QuanLyHoaDon.class);
                startActivity(intent);
            }
        };
        if (cvquanlyhoadon != null) cvquanlyhoadon.setOnClickListener(listenerHoaDon);
        if (btnImgHoaDon != null) btnImgHoaDon.setOnClickListener(listenerHoaDon);

        //----------------------------------------------------------------------------------

        View.OnClickListener listenerThongKe = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManHinhChinh.this, ThongKe.class);
                startActivity(intent);
            }
        };
        if (cvthongke != null) cvthongke.setOnClickListener(listenerThongKe);
        if (btnImgThongKe != null) btnImgThongKe.setOnClickListener(listenerThongKe);

        //------------------------------------------------------------------------------------

        if (btndangxuat != null) {
            btndangxuat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ManHinhChinh.this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ManHinhChinh.this, ManHinhDangNhap.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }
}
