package com.example.quanlyphongtro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import com.example.quanlyphongtro.model.ModelNguoiThue;
import com.example.quanlyphongtro.model.ModelPhong;

public class ManHinhChinh extends AppCompatActivity {

    // Khai báo các view
    CardView cvquanlyphongtro, cvquanlynguoithue, cvquanlyhoadon, cvquanlyhopdong, cvquanlydiennuoc, cvquanlythuchi, cvthongke;
    ImageButton btnImgQuanLyPhong, btnImgNguoiThue, btnImgHoaDon, btnImgHopDong, btnImgDienNuoc, btnImgThuChi, btnImgThongKe;
    Button btndangxuat;
    TextView txtSoPhongTrong, txtSoPhongDangThue, txtSoNguoiThue;

    QuanLyCoSoDuLieu db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giaodientrangchu);

        db = new QuanLyCoSoDuLieu(this);

        // Ánh xạ TextView thống kê
        txtSoPhongTrong = findViewById(R.id.txtsophongtrong);
        txtSoPhongDangThue = findViewById(R.id.txtsophongdangthue);
        txtSoNguoiThue = findViewById(R.id.txtsonguoithue);

        // Ánh xạ các CardView
        cvquanlyphongtro = findViewById(R.id.cvquanlyphongtro);
        cvquanlynguoithue = findViewById(R.id.cvquanlynguoithue);
        cvquanlyhoadon = findViewById(R.id.cvquanlyhoadon);
        cvquanlyhopdong = findViewById(R.id.cvquanlyhopdong);
        cvquanlydiennuoc = findViewById(R.id.cvquanlydiennuoc);
        cvquanlythuchi = findViewById(R.id.cvquanlythuchi);
        cvthongke = findViewById(R.id.cvthongke);

        // Ánh xạ các ImageButton
        btnImgQuanLyPhong = findViewById(R.id.btnimgquanlyphongtro);
        btnImgNguoiThue = findViewById(R.id.btnimgnguoithue);
        btnImgHoaDon = findViewById(R.id.btnimghoadon);
        btnImgHopDong = findViewById(R.id.btnimghopdong);
        btnImgDienNuoc = findViewById(R.id.btnimgdiennuoc);
        btnImgThuChi = findViewById(R.id.btnimgthuchi);
        btnImgThongKe = findViewById(R.id.btnimgthongke);
        
        // Ánh xạ nút Đăng xuất
        btndangxuat = findViewById(R.id.btndangxuat);

        // 1. Quản lý phòng trọ
        View.OnClickListener listenerQuanLyPhong = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManHinhChinh.this, QuanLyPhong.class);
                startActivity(intent);
            }
        };
        if (cvquanlyphongtro != null) cvquanlyphongtro.setOnClickListener(listenerQuanLyPhong);
        if (btnImgQuanLyPhong != null) btnImgQuanLyPhong.setOnClickListener(listenerQuanLyPhong);

        // 2. Quản lý người thuê
        View.OnClickListener listenerNguoiThue = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManHinhChinh.this, QuanLyNguoiThue.class);
                startActivity(intent);
            }
        };
        if (cvquanlynguoithue != null) cvquanlynguoithue.setOnClickListener(listenerNguoiThue);
        if (btnImgNguoiThue != null) btnImgNguoiThue.setOnClickListener(listenerNguoiThue);

        // 3. Quản lý hóa đơn
        View.OnClickListener listenerHoaDon = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManHinhChinh.this, QuanLyHoaDon.class);
                startActivity(intent);
            }
        };
        if (cvquanlyhoadon != null) cvquanlyhoadon.setOnClickListener(listenerHoaDon);
        if (btnImgHoaDon != null) btnImgHoaDon.setOnClickListener(listenerHoaDon);

        // 4. Quản lý hợp đồng
        View.OnClickListener listenerHopDong = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManHinhChinh.this, QuanLyHopDong.class);
                startActivity(intent);
            }
        };
        if (cvquanlyhopdong != null) cvquanlyhopdong.setOnClickListener(listenerHopDong);
        if (btnImgHopDong != null) btnImgHopDong.setOnClickListener(listenerHopDong);

        // 5. Quản lý điện nước & dịch vụ
        View.OnClickListener listenerDienNuoc = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManHinhChinh.this, QuanLyDienNuoc.class);
                startActivity(intent);
            }
        };
        if (cvquanlydiennuoc != null) cvquanlydiennuoc.setOnClickListener(listenerDienNuoc);
        if (btnImgDienNuoc != null) btnImgDienNuoc.setOnClickListener(listenerDienNuoc);

        // 6. Sổ thu chi & sửa chữa
        View.OnClickListener listenerThuChi = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManHinhChinh.this, QuanLyThuChi.class);
                startActivity(intent);
            }
        };
        if (cvquanlythuchi != null) cvquanlythuchi.setOnClickListener(listenerThuChi);
        if (btnImgThuChi != null) btnImgThuChi.setOnClickListener(listenerThuChi);

        // 7. Thống kê
        View.OnClickListener listenerThongKe = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManHinhChinh.this, ThongKe.class);
                startActivity(intent);
            }
        };
        if (cvthongke != null) cvthongke.setOnClickListener(listenerThongKe);
        if (btnImgThongKe != null) btnImgThongKe.setOnClickListener(listenerThongKe);

        // 8. Đăng xuất
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

    @Override
    protected void onResume() {
        super.onResume();
        capNhatThongKeNhanh();
    }

    private void capNhatThongKeNhanh() {
        if (db == null) return;

        List<ModelPhong> danhSachPhong = db.getAllPhong();
        int soPhongTrong = 0;
        int soPhongDangThue = 0;

        if (danhSachPhong != null) {
            for (ModelPhong p : danhSachPhong) {
                if (p.getTrangThai() != null) {
                    String tt = p.getTrangThai().toLowerCase();
                    if (tt.contains("trống")) {
                        soPhongTrong++;
                    } else if (tt.contains("thuê")) {
                        soPhongDangThue++;
                    }
                }
            }
        }

        // Tính tổng số lượng người ở từ các hồ sơ người thuê
        List<ModelNguoiThue> danhSachNguoiThue = db.getAllNguoiThue();
        int tongSoNguoiO = 0;
        if (danhSachNguoiThue != null) {
            for (ModelNguoiThue nt : danhSachNguoiThue) {
                try {
                    String slStr = nt.getSoLuongNguoi();
                    if (slStr != null && !slStr.isEmpty()) {
                        tongSoNguoiO += Integer.parseInt(slStr);
                    } else {
                        tongSoNguoiO += 1;
                    }
                } catch (NumberFormatException e) {
                    tongSoNguoiO += 1;
                }
            }
        }

        // Hiển thị lên TextView tương ứng trên màn hình chính
        if (txtSoPhongTrong != null) {
            txtSoPhongTrong.setText(String.valueOf(soPhongTrong));
        }
        if (txtSoPhongDangThue != null) {
            txtSoPhongDangThue.setText(String.valueOf(soPhongDangThue));
        }
        if (txtSoNguoiThue != null) {
            txtSoNguoiThue.setText(String.valueOf(tongSoNguoiO));
        }
    }
}
