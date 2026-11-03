package com.example.quanlyphongtro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class ManHinhChinh extends AppCompatActivity {
    
    ImageButton btnImgQuanLyPhong;
    CardView cvQuanLyPhong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giaodientrangchu);
        
        // Ánh xạ View
        btnImgQuanLyPhong = findViewById(R.id.btnimgquanlyphongtro);
        // Tạo sự kiện chuyển trang quan ly phong
       btnImgQuanLyPhong.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(ManHinhChinh.this, QuanLyPhong.class);
               startActivity(intent);
           }
       });

    }
}
