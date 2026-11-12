package com.example.quanlyphongtro;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.List;

import com.example.quanlyphongtro.model.ModelNguoiThue;
import com.example.quanlyphongtro.model.ModelPhong;

public class ThongKe extends AppCompatActivity {

    TextView txtThongKeDoanhThu, txtThongKeTongPhong, txtThongKePhongDangThue;
    TextView txtThongKePhongTrong, txtThongKeTyLeLapDay, txtThongKeTongNguoiThue, txtThongKeTongHopDong;
    Button btnQuayLaiThongKe;

    QuanLyCoSoDuLieu dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giaodienthongke);

        dbHelper = new QuanLyCoSoDuLieu(this);

        // Ánh xạ View
        txtThongKeDoanhThu = findViewById(R.id.txtThongKeDoanhThu);
        txtThongKeTongPhong = findViewById(R.id.txtThongKeTongPhong);
        txtThongKePhongDangThue = findViewById(R.id.txtThongKePhongDangThue);
        txtThongKePhongTrong = findViewById(R.id.txtThongKePhongTrong);
        txtThongKeTyLeLapDay = findViewById(R.id.txtThongKeTyLeLapDay);
        txtThongKeTongNguoiThue = findViewById(R.id.txtThongKeTongNguoiThue);
        txtThongKeTongHopDong = findViewById(R.id.txtThongKeTongHopDong);
        btnQuayLaiThongKe = findViewById(R.id.btnQuayLaiThongKe);

        // Tính toán và nạp dữ liệu thống kê
        thucHienThongKe();

        btnQuayLaiThongKe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void thucHienThongKe() {
        // 1. Lấy dữ liệu phòng trọ
        List<ModelPhong> listPhong = dbHelper.getAllPhong();
        int tongPhong = listPhong.size();
        int phongDangThue = 0;
        int phongTrong = 0;
        double tongDoanhThuUocTinh = 0;

        for (ModelPhong p : listPhong) {
            if (p.getTrangThai().toLowerCase().contains("đang thuê") || p.getTrangThai().toLowerCase().contains("thuê")) {
                phongDangThue++;
                try {
                    tongDoanhThuUocTinh += Double.parseDouble(p.getGiaThue());
                } catch (NumberFormatException ignored) {}
            } else {
                phongTrong++;
            }
        }

        // Tính tỷ lệ lấp đầy
        int tyLeLapDay = tongPhong > 0 ? (phongDangThue * 100 / tongPhong) : 0;

        // 2. Lấy dữ liệu người thuê & hợp đồng
        List<ModelNguoiThue> listNguoiThue = dbHelper.getAllNguoiThue();
        int tongNguoiThue = listNguoiThue.size();

        int tongHopDong = dbHelper.getAllHopDong().size();

        // 3. Hiển thị lên giao diện
        txtThongKeDoanhThu.setText(String.format("%,.0f VNĐ", tongDoanhThuUocTinh));
        txtThongKeTongPhong.setText(String.valueOf(tongPhong));
        txtThongKePhongDangThue.setText(String.valueOf(phongDangThue));
        txtThongKePhongTrong.setText(String.valueOf(phongTrong));
        txtThongKeTyLeLapDay.setText(tyLeLapDay + "%");
        txtThongKeTongNguoiThue.setText(String.valueOf(tongNguoiThue));
        txtThongKeTongHopDong.setText(String.valueOf(tongHopDong));
    }
}
