package com.example.quanlyphongtro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.example.quanlyphongtro.adapter.NguoiThueAdapter;
import com.example.quanlyphongtro.model.ModelNguoiThue;
import com.example.quanlyphongtro.model.ModelPhong;

public class QuanLyNguoiThue extends AppCompatActivity {

    // Khai báo các view nhập liệu và nút bấm
    TextInputEditText edtHoTen, edtSoDienThoai, edtCCCD, edtQueQuan, edtNgaySinh, edtNgayChuyenVao, edtNgayChuyenDi, edtTimKiem;
    AutoCompleteTextView dropdownPhongThue;
    Button btnThemNguoiThue, btnSuaNguoiThue, btnXoaNguoiThue, btnLamMoiNguoiThue, btnQuayLaiNguoiThue;
    RecyclerView recyclerDanhSachNguoiThue;

    QuanLyCoSoDuLieu db;
    List<ModelNguoiThue> listNguoiThue;
    NguoiThueAdapter nguoiThueAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giaodiennguoithue);

        // Khởi tạo Database
        db = new QuanLyCoSoDuLieu(this);

        // Ánh xạ các thành phần giao diện
        edtHoTen = findViewById(R.id.edtHoTen);
        edtSoDienThoai = findViewById(R.id.edtSoDienThoai);
        edtCCCD = findViewById(R.id.edtCCCD);
        edtNgaySinh = findViewById(R.id.edtNgaySinh);
        dropdownPhongThue = findViewById(R.id.dropdownPhongThue);
        edtQueQuan = findViewById(R.id.edtQueQuan);
        edtNgayChuyenVao = findViewById(R.id.edtNgayChuyenVao);
        edtNgayChuyenDi = findViewById(R.id.edtNgayChuyenDi);
        edtTimKiem = findViewById(R.id.edtTimKiem);

        btnThemNguoiThue = findViewById(R.id.btnThemNguoiThue);
        btnSuaNguoiThue = findViewById(R.id.btnSuaNguoiThue);
        btnXoaNguoiThue = findViewById(R.id.btnXoaNguoiThue);
        btnLamMoiNguoiThue = findViewById(R.id.btnLamMoiNguoiThue);
        btnQuayLaiNguoiThue = findViewById(R.id.btnQuayLaiNguoiThue);
        recyclerDanhSachNguoiThue = findViewById(R.id.recyclerDanhSachNguoiThue);

        // Thiết lập sự kiện chọn ngày tháng (DatePickerDialog)
        edtNgaySinh.setOnClickListener(v -> hienThiChonNgay(edtNgaySinh));
        edtNgayChuyenVao.setOnClickListener(v -> hienThiChonNgay(edtNgayChuyenVao));
        edtNgayChuyenDi.setOnClickListener(v -> hienThiChonNgay(edtNgayChuyenDi));

        // Tải danh sách phòng trống và danh sách người thuê
        loadDanhSachPhongTrong();
        loadDanhSachNguoiThue();

        // Tính năng Tìm kiếm theo Tên hoặc Số điện thoại
        edtTimKiem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                timKiemNguoiThue(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Nút THÊM người thuê
        btnThemNguoiThue.setOnClickListener(v -> {
            String hoTen = edtHoTen.getText().toString().trim();
            String sdt = edtSoDienThoai.getText().toString().trim();
            String cccd = edtCCCD.getText().toString().trim();
            String ngaySinh = edtNgaySinh.getText().toString().trim();
            String phongThue = dropdownPhongThue.getText().toString().trim();
            String queQuan = edtQueQuan.getText().toString().trim();
            String ngayVao = edtNgayChuyenVao.getText().toString().trim();
            String ngayDi = edtNgayChuyenDi.getText().toString().trim();

            if (hoTen.isEmpty() || sdt.isEmpty() || cccd.isEmpty() || phongThue.isEmpty() || phongThue.contains("Không có")) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin bắt buộc và chọn phòng!", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean kq = db.themNguoiThue(hoTen, sdt, cccd, phongThue, queQuan, ngaySinh, ngayVao, ngayDi);
            if (kq) {
                // Cập nhật trạng thái phòng thành "Đang thuê"
                String maPhong = phongThue.split(" ")[0].trim();
                db.updateTrangThaiPhong(maPhong, "Đang thuê");

                Toast.makeText(this, "Thêm người thuê thành công!", Toast.LENGTH_SHORT).show();
                loadDanhSachNguoiThue();
                loadDanhSachPhongTrong();
                lamMoiForm();
            } else {
                Toast.makeText(this, "Thêm thất bại (Số CCCD đã tồn tại)!", Toast.LENGTH_SHORT).show();
            }
        });

        // Nút SỬA thông tin người thuê
        btnSuaNguoiThue.setOnClickListener(v -> {
            String hoTen = edtHoTen.getText().toString().trim();
            String sdt = edtSoDienThoai.getText().toString().trim();
            String cccd = edtCCCD.getText().toString().trim();
            String ngaySinh = edtNgaySinh.getText().toString().trim();
            String phongThue = dropdownPhongThue.getText().toString().trim();
            String queQuan = edtQueQuan.getText().toString().trim();
            String ngayVao = edtNgayChuyenVao.getText().toString().trim();
            String ngayDi = edtNgayChuyenDi.getText().toString().trim();

            if (cccd.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn người thuê cần sửa từ danh sách!", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean kq = db.suaNguoiThue(hoTen, sdt, cccd, phongThue, queQuan, ngaySinh, ngayVao, ngayDi);
            if (kq) {
                Toast.makeText(this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
                loadDanhSachNguoiThue();
                lamMoiForm();
            } else {
                Toast.makeText(this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
            }
        });

        // Nút XÓA người thuê
        btnXoaNguoiThue.setOnClickListener(v -> {
            String cccd = edtCCCD.getText().toString().trim();
            String phongHienTai = dropdownPhongThue.getText().toString().trim();

            if (cccd.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn người thuê cần xóa từ danh sách!", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean kq = db.xoaNguoiThue(cccd);
            if (kq) {
                // Trả trạng thái phòng về "Còn trống"
                if (!phongHienTai.isEmpty() && !phongHienTai.contains("Không có")) {
                    String maPhong = phongHienTai.split(" ")[0].trim();
                    db.updateTrangThaiPhong(maPhong, "Còn trống");
                }

                Toast.makeText(this, "Xóa người thuê thành công!", Toast.LENGTH_SHORT).show();
                loadDanhSachNguoiThue();
                loadDanhSachPhongTrong();
                lamMoiForm();
            } else {
                Toast.makeText(this, "Xóa thất bại!", Toast.LENGTH_SHORT).show();
            }
        });

        // Nút LÀM MỚI form
        btnLamMoiNguoiThue.setOnClickListener(v -> lamMoiForm());

        // Nút QUAY LẠI
        btnQuayLaiNguoiThue.setOnClickListener(v -> finish());
    }

    // --- HÀM HIỂN THỊ BẢNG CHỌN NGÀY THÁNG (DatePickerDialog) ---
    private void hienThiChonNgay(TextInputEditText editText) {
        Calendar lich = Calendar.getInstance();
        int nam = lich.get(Calendar.YEAR);
        int thang = lich.get(Calendar.MONTH);
        int ngay = lich.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialogChonNgay = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String ngayThangNam = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, (month + 1), year);
            editText.setText(ngayThangNam);
        }, nam, thang, ngay);

        dialogChonNgay.show();
    }

    // --- HÀM TẢI DANH SÁCH PHÒNG TRỐNG ---
    private void loadDanhSachPhongTrong() {
        List<ModelPhong> tatCaPhong = db.getAllPhong();
        List<String> phongTrong = new ArrayList<>();

        for (ModelPhong p : tatCaPhong) {
            if (p.getTrangThai().toLowerCase().contains("trống")) {
                phongTrong.add(p.getMaPhong() + " - " + p.getTenPhong());
            }
        }

        if (phongTrong.isEmpty()) {
            phongTrong.add("Không có phòng trống");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, phongTrong);
        dropdownPhongThue.setAdapter(adapter);
    }

    // --- HÀM TẢI DANH SÁCH NGƯỜI THUÊ LÊN RECYCLERVIEW ---
    private void loadDanhSachNguoiThue() {
        listNguoiThue = db.getAllNguoiThue();
        nguoiThueAdapter = new NguoiThueAdapter(this, listNguoiThue, item -> {
            // Khi bấm vào 1 dòng trong danh sách -> Đổ dữ liệu lên form để Sửa/Xóa
            edtHoTen.setText(item.getHoTen());
            edtSoDienThoai.setText(item.getSoDienThoai());
            edtCCCD.setText(item.getCccd());
            edtCCCD.setEnabled(false); // Không cho sửa CCCD khi đang cập nhật
            edtNgaySinh.setText(item.getNgaySinh());
            dropdownPhongThue.setText(item.getPhongThue(), false);
            edtQueQuan.setText(item.getQueQuan());
            edtNgayChuyenVao.setText(item.getNgayChuyenVao());
            edtNgayChuyenDi.setText(item.getNgayChuyenDi());
        });
        recyclerDanhSachNguoiThue.setLayoutManager(new LinearLayoutManager(this));
        recyclerDanhSachNguoiThue.setAdapter(nguoiThueAdapter);
    }

    // --- HÀM TÌM KIẾM THEO TÊN HOẶC SỐ ĐIỆN THOẠI ---
    private void timKiemNguoiThue(String tuKhoa) {
        String tuKhoaChuan = tuKhoa.toLowerCase().trim();
        List<ModelNguoiThue> danhSachKetQua = new ArrayList<>();

        for (ModelNguoiThue item : db.getAllNguoiThue()) {
            if (item.getHoTen().toLowerCase().contains(tuKhoaChuan) ||
                item.getSoDienThoai().contains(tuKhoaChuan)) {
                danhSachKetQua.add(item);
            }
        }

        nguoiThueAdapter = new NguoiThueAdapter(this, danhSachKetQua, item -> {
            edtHoTen.setText(item.getHoTen());
            edtSoDienThoai.setText(item.getSoDienThoai());
            edtCCCD.setText(item.getCccd());
            edtCCCD.setEnabled(false);
            edtNgaySinh.setText(item.getNgaySinh());
            dropdownPhongThue.setText(item.getPhongThue(), false);
            edtQueQuan.setText(item.getQueQuan());
            edtNgayChuyenVao.setText(item.getNgayChuyenVao());
            edtNgayChuyenDi.setText(item.getNgayChuyenDi());
        });
        recyclerDanhSachNguoiThue.setAdapter(nguoiThueAdapter);
    }

    // --- HÀM LÀM MỚI FORM NHẬP LIỆU ---
    private void lamMoiForm() {
        edtHoTen.setText("");
        edtSoDienThoai.setText("");
        edtCCCD.setText("");
        edtCCCD.setEnabled(true); // Cho phép nhập lại CCCD khi thêm mới
        edtNgaySinh.setText("");
        dropdownPhongThue.setText("", false);
        edtQueQuan.setText("");
        edtNgayChuyenVao.setText("");
        edtNgayChuyenDi.setText("");
        edtTimKiem.setText("");
        edtHoTen.requestFocus();
        loadDanhSachPhongTrong();
        loadDanhSachNguoiThue();
    }
}
