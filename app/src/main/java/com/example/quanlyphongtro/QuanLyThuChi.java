package com.example.quanlyphongtro;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class QuanLyThuChi extends AppCompatActivity {

    TextInputEditText txtDanhMuc, txtSoTien, txtGhiChuThuChi;
    Button btnGhiChi, btnGhiThu, btnQuayLaiThuChi;
    TextView txtTongChiPhi;
    ListView lvThuChi;

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
        lvThuChi = findViewById(R.id.lvThuChi);

        // Load tổng chi phí và lịch sử thu chi
        capNhatTongChiPhi();
        loadLichSuThuChi();

        // Sự kiện Ghi khoản chi
        btnGhiChi.setOnClickListener(v -> ghiNhanThuChi("CHI"));

        // Sự kiện Ghi khoản thu
        btnGhiThu.setOnClickListener(v -> ghiNhanThuChi("THU"));

        btnQuayLaiThuChi.setOnClickListener(v -> finish());
    }

    private void ghiNhanThuChi(String loai) {
        String danhMuc = txtDanhMuc.getText() != null ? txtDanhMuc.getText().toString().trim() : "";
        String soTienStr = txtSoTien.getText() != null ? txtSoTien.getText().toString().trim() : "";
        String ghiChu = txtGhiChuThuChi.getText() != null ? txtGhiChuThuChi.getText().toString().trim() : "";

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
            loadLichSuThuChi();
        } else {
            Toast.makeText(this, "Ghi nhận thất bại!", Toast.LENGTH_SHORT).show();
        }
    }

    private void capNhatTongChiPhi() {
        double tongChi = dbHelper.getTongChiPhi();
        txtTongChiPhi.setText(String.format(Locale.getDefault(), "%,.0f VNĐ", tongChi));
    }

    private void loadLichSuThuChi() {
        if (lvThuChi != null) {
            List<String> list = dbHelper.getAllThuChiString();
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
            lvThuChi.setAdapter(adapter);
        }
    }
}
