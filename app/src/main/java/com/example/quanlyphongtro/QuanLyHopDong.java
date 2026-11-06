package com.example.quanlyphongtro;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

import com.example.quanlyphongtro.model.ModelNguoiThue;
import com.example.quanlyphongtro.model.ModelPhong;

public class QuanLyHopDong extends AppCompatActivity {

    TextInputEditText txtMahopdong, txtNguoiocung, txtNgaybatdau, txtNgayketthuc;
    TextInputEditText txtGiathue, txtTiencoc, txtNgaythanhtoanhangthang, txtNgaythanhtoandautien, txtGhichu;
    Spinner spnPhongthue, spnNguoidaidien;
    Button btnTaohopdong;
    ListView lvHopdong;

    QuanLyCoSoDuLieu dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giaodienhopdong);

        dbHelper = new QuanLyCoSoDuLieu(this);

        // Ánh xạ View
        txtMahopdong = findViewById(R.id.txtmahopdong);
        spnPhongthue = findViewById(R.id.spnphongthue);
        spnNguoidaidien = findViewById(R.id.spnnguoidaidien);
        txtNguoiocung = findViewById(R.id.txtnguoiocung);
        txtNgaybatdau = findViewById(R.id.txtngaybatdau);
        txtNgayketthuc = findViewById(R.id.txtngayketthuc);
        txtGiathue = findViewById(R.id.txtgiathue);
        txtTiencoc = findViewById(R.id.txttiencoc);
        txtNgaythanhtoanhangthang = findViewById(R.id.txtngaythanhtoanhangthang);
        txtNgaythanhtoandautien = findViewById(R.id.txtngaythanhtoandautien);
        txtGhichu = findViewById(R.id.txtghichu);

        btnTaohopdong = findViewById(R.id.btntaohoadon);
        lvHopdong = findViewById(R.id.lvhopdong);

        // Nạp dữ liệu vào Spinner
        loadSpinnerData();
        // Load danh sách hợp đồng
        loadDanhSachHopDong();

        // Sự kiện Lưu hợp đồng
        btnTaohopdong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String maHD = txtMahopdong.getText().toString().trim();
                String nguoiOCung = txtNguoiocung.getText().toString().trim();
                String ngayBD = txtNgaybatdau.getText().toString().trim();
                String ngayKT = txtNgayketthuc.getText().toString().trim();
                String giaStr = txtGiathue.getText().toString().trim();
                String cocStr = txtTiencoc.getText().toString().trim();
                String ngayTTThang = txtNgaythanhtoanhangthang.getText().toString().trim();
                String ngayTTDau = txtNgaythanhtoandautien.getText().toString().trim();
                String ghiChu = txtGhichu.getText().toString().trim();

                if (maHD.isEmpty() || ngayBD.isEmpty() || giaStr.isEmpty() || cocStr.isEmpty()) {
                    Toast.makeText(QuanLyHopDong.this, "Vui lòng nhập đầy đủ thông tin bắt buộc!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String phongChon = spnPhongthue.getSelectedItem() != null ? spnPhongthue.getSelectedItem().toString() : "";
                String nguoiChon = spnNguoidaidien.getSelectedItem() != null ? spnNguoidaidien.getSelectedItem().toString() : "";

                if (phongChon.contains("Không có") || nguoiChon.contains("Không có")) {
                    Toast.makeText(QuanLyHopDong.this, "Cần có ít nhất 1 phòng trống và 1 người thuê!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String maPhong = phongChon.split(" ")[0].trim();
                double giaThue = Double.parseDouble(giaStr);
                double tienCoc = Double.parseDouble(cocStr);

                // Lưu vào CSDL
                boolean success = dbHelper.themHopDongChiTiet(maHD, maPhong, nguoiChon, nguoiOCung, ngayBD, ngayKT, giaThue, tienCoc, ngayTTThang, ngayTTDau, ghiChu);
                if (success) {
                    dbHelper.updateTrangThaiPhong(maPhong, "Đang thuê");
                    Toast.makeText(QuanLyHopDong.this, "Tạo hợp đồng thành công!", Toast.LENGTH_SHORT).show();
                    loadDanhSachHopDong();
                    loadSpinnerData();
                    xoaTrangForm();
                } else {
                    Toast.makeText(QuanLyHopDong.this, "Tạo thất bại! Mã hợp đồng có thể bị trùng.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadSpinnerData() {
        List<ModelPhong> allPhong = dbHelper.getAllPhong();
        List<String> phongTrong = new ArrayList<>();
        for (ModelPhong p : allPhong) {
            if (p.getTrangThai().toLowerCase().contains("trống")) {
                phongTrong.add(p.getMaPhong() + " (" + p.getTenPhong() + ")");
            }
        }
        if (phongTrong.isEmpty()) phongTrong.add("Không có phòng trống");
        ArrayAdapter<String> adapterPhong = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, phongTrong);
        adapterPhong.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnPhongthue.setAdapter(adapterPhong);

        List<ModelNguoiThue> allNguoiThue = dbHelper.getAllNguoiThue();
        List<String> tenNguoiThue = new ArrayList<>();
        for (ModelNguoiThue nt : allNguoiThue) {
            tenNguoiThue.add(nt.getHoTen() + " - " + nt.getSoDienThoai());
        }
        if (tenNguoiThue.isEmpty()) tenNguoiThue.add("Không có người thuê nào");
        ArrayAdapter<String> adapterNguoiThue = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tenNguoiThue);
        adapterNguoiThue.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnNguoidaidien.setAdapter(adapterNguoiThue);
    }

    private void loadDanhSachHopDong() {
        List<String> list = dbHelper.getAllHopDongString();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        lvHopdong.setAdapter(adapter);
    }

    private void xoaTrangForm() {
        txtMahopdong.setText("");
        txtNguoiocung.setText("");
        txtNgaybatdau.setText("");
        txtNgayketthuc.setText("");
        txtGiathue.setText("");
        txtTiencoc.setText("");
        txtNgaythanhtoanhangthang.setText("");
        txtNgaythanhtoandautien.setText("");
        txtGhichu.setText("");
        txtMahopdong.requestFocus();
    }
}
