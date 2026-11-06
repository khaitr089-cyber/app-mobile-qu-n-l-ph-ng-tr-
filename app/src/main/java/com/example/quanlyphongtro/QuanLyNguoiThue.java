package com.example.quanlyphongtro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
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

    // Khai báo các thành phần giao diện người thuê
    TextInputEditText edtHoTen, edtSoDienThoai, edtCCCD, edtNgaySinh, edtQueQuan, edtNgayChuyenVao, edtNgayChuyenDi;
    AutoCompleteTextView dropdownPhongThue;
    Button btnThemNguoiThue, btnSuaNguoiThue, btnXoaNguoiThue, btnLamMoiNguoiThue, btnQuayLaiNguoiThue;
    RecyclerView recyclerDanhSachNguoiThue;

    // Khai báo đối tượng SQLite và Adapter
    QuanLyCoSoDuLieu db;
    List<ModelNguoiThue> listNguoiThue;
    NguoiThueAdapter nguoiThueAdapter;

    // Biến ghi nhớ CCCD đang được chọn để Sửa hoặc Xóa chính xác đối tượng
    private String selectedCccd = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giaodiennguoithue);

        // Khởi tạo database
        db = new QuanLyCoSoDuLieu(this);

        // --- BƯỚC 1: ÁNH XẠ CÁC VIEW TỪ XML SANG JAVA ---
        edtHoTen = findViewById(R.id.edtHoTen);
        edtSoDienThoai = findViewById(R.id.edtSoDienThoai);
        edtCCCD = findViewById(R.id.edtCCCD);
        edtNgaySinh = findViewById(R.id.edtNgaySinh);
        dropdownPhongThue = findViewById(R.id.dropdownPhongThue);
        edtQueQuan = findViewById(R.id.edtQueQuan);
        edtNgayChuyenVao = findViewById(R.id.edtNgayChuyenVao);
        edtNgayChuyenDi = findViewById(R.id.edtNgayChuyenDi);

        btnThemNguoiThue = findViewById(R.id.btnThemNguoiThue);
        btnSuaNguoiThue = findViewById(R.id.btnSuaNguoiThue);
        btnXoaNguoiThue = findViewById(R.id.btnXoaNguoiThue);
        btnLamMoiNguoiThue = findViewById(R.id.btnLamMoiNguoiThue);
        btnQuayLaiNguoiThue = findViewById(R.id.btnQuayLaiNguoiThue);
        recyclerDanhSachNguoiThue = findViewById(R.id.recyclerDanhSachNguoiThue);

        // --- BƯỚC 2: THIẾT LẬP CHỌN LỊCH (DatePickerDialog) CHO CÁC Ô NGÀY THÁNG ---
        edtNgaySinh.setOnClickListener(v -> showDatePickerDialog(edtNgaySinh));
        edtNgayChuyenVao.setOnClickListener(v -> showDatePickerDialog(edtNgayChuyenVao));
        edtNgayChuyenDi.setOnClickListener(v -> showDatePickerDialog(edtNgayChuyenDi));

        // Nạp danh sách phòng còn trống vào Dropdown chọn phòng
        loadDanhSachPhongTrong();

        // Tải danh sách người thuê từ SQLite lên màn hình
        loadDanhSachNguoiThue();

        // --- BƯỚC 3: CHỨC NĂNG THÊM NGƯỜI THUÊ (Kiểm tra trùng CCCD) ---
        btnThemNguoiThue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hoTen = edtHoTen.getText().toString().trim();
                String sdt = edtSoDienThoai.getText().toString().trim();
                String cccd = edtCCCD.getText().toString().trim();
                String ngaySinh = edtNgaySinh.getText().toString().trim();
                String phongThue = dropdownPhongThue.getText().toString().trim();
                String queQuan = edtQueQuan.getText().toString().trim();
                String ngayVao = edtNgayChuyenVao.getText().toString().trim();
                String ngayDi = edtNgayChuyenDi.getText().toString().trim();

                // Kiểm tra rỗng
                if (hoTen.isEmpty() || sdt.isEmpty() || cccd.isEmpty() || phongThue.isEmpty() || queQuan.isEmpty() || phongThue.contains("Không có")) {
                    Toast.makeText(QuanLyNguoiThue.this, "Vui lòng nhập đầy đủ thông tin hoặc chọn phòng trống!", Toast.LENGTH_SHORT).show();
                } else {
                    // Gọi hàm thêm vào SQLite (Đã truyền đầy đủ ngày sinh, ngày vào, ngày đi)
                    boolean check = db.themNguoiThue(hoTen, sdt, cccd, phongThue, queQuan, ngaySinh, ngayVao, ngayDi);
                    if (check) {
                        // Tách lấy mã phòng (VD: "P101 (Phòng đơn)" -> lấy "P101")
                        String maPhong = phongThue;
                        if (phongThue.contains(" ")) {
                            maPhong = phongThue.split(" ")[0].trim();
                        }
                        // Cập nhật trạng thái phòng đó thành "Đang thuê" trong cơ sở dữ liệu
                        db.updateTrangThaiPhong(maPhong, "Đang thuê");

                        Toast.makeText(QuanLyNguoiThue.this, "Thêm người thuê thành công và đã cập nhật phòng!", Toast.LENGTH_SHORT).show();
                        loadDanhSachNguoiThue(); // Tải lại danh sách người thuê
                        loadDanhSachPhongTrong();// Cập nhật lại danh sách phòng trống
                        lamMoiForm();            // Xóa trắng form
                    } else {
                        Toast.makeText(QuanLyNguoiThue.this, "Thêm thất bại (CCCD đã tồn tại)!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // --- BƯỚC 4: CHỨC NĂNG SỬA THÔNG TIN NGƯỜI THUÊ (Dựa vào selectedCccd) ---
        btnSuaNguoiThue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedCccd.isEmpty()) {
                    Toast.makeText(QuanLyNguoiThue.this, "Vui lòng chọn người thuê cần sửa từ danh sách!", Toast.LENGTH_SHORT).show();
                } else {
                    String hoTen = edtHoTen.getText().toString().trim();
                    String sdt = edtSoDienThoai.getText().toString().trim();
                    String newCccd = edtCCCD.getText().toString().trim();
                    String ngaySinh = edtNgaySinh.getText().toString().trim();
                    String phongThue = dropdownPhongThue.getText().toString().trim();
                    String queQuan = edtQueQuan.getText().toString().trim();
                    String ngayVao = edtNgayChuyenVao.getText().toString().trim();
                    String ngayDi = edtNgayChuyenDi.getText().toString().trim();

                    // Gọi hàm sửa trong SQLite theo CCCD cũ (selectedCccd)
                    boolean check = db.suaNguoiThue(selectedCccd, hoTen, sdt, newCccd, phongThue, queQuan, ngaySinh, ngayVao, ngayDi);
                    if (check) {
                        Toast.makeText(QuanLyNguoiThue.this, "Sửa thông tin thành công!", Toast.LENGTH_SHORT).show();
                        loadDanhSachNguoiThue();
                        lamMoiForm();
                    } else {
                        Toast.makeText(QuanLyNguoiThue.this, "Sửa thất bại!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // --- BƯỚC 5: CHỨC NĂNG XÓA NGƯỜI THUÊ (Chỉ xóa đúng người đang chọn) ---
        btnXoaNguoiThue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedCccd.isEmpty()) {
                    Toast.makeText(QuanLyNguoiThue.this, "Vui lòng chọn người thuê cần xóa từ danh sách!", Toast.LENGTH_SHORT).show();
                } else {
                    String phongThueHienTai = dropdownPhongThue.getText().toString().trim();

                    // Gọi hàm xóa khỏi SQLite theo CCCD (selectedCccd)
                    boolean check = db.xoaNguoiThue(selectedCccd);
                    if (check) {
                        // Nếu xóa người thuê, trả trạng thái phòng về "Còn trống"
                        if (!phongThueHienTai.isEmpty() && !phongThueHienTai.contains("Không có")) {
                            String maPhong = phongThueHienTai.split(" ")[0].trim();
                            db.updateTrangThaiPhong(maPhong, "Còn trống");
                        }

                        Toast.makeText(QuanLyNguoiThue.this, "Xóa người thuê thành công!", Toast.LENGTH_SHORT).show();
                        loadDanhSachNguoiThue();
                        loadDanhSachPhongTrong();
                        lamMoiForm();
                    } else {
                        Toast.makeText(QuanLyNguoiThue.this, "Xóa thất bại!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // --- BƯỚC 6: LÀM MỚI FORM ---
        btnLamMoiNguoiThue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lamMoiForm();
                Toast.makeText(QuanLyNguoiThue.this, "Đã làm mới form", Toast.LENGTH_SHORT).show();
            }
        });

        // --- BƯỚC 7: QUAY LẠI MÀN HÌNH CHÍNH ---
        btnQuayLaiNguoiThue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // --- HÀM HIỂN THỊ HỘP THOẠI CHỌN LỊCH (DatePickerDialog) ---
    private void showDatePickerDialog(TextInputEditText edt) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            String dateStr = String.format(Locale.getDefault(), "%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
            edt.setText(dateStr);
        }, year, month, day);

        datePickerDialog.show();
    }

    // --- HÀM NẠP DANH SÁCH PHÒNG CÒN TRỐNG VÀO DROPDOWN ---
    private void loadDanhSachPhongTrong() {
        List<ModelPhong> allPhong = db.getAllPhong();
        List<String> phongConTrong = new ArrayList<>();

        for (ModelPhong p : allPhong) {
            if (p.getTrangThai().toLowerCase().contains("trống")) {
                phongConTrong.add(p.getMaPhong() + " (" + p.getTenPhong() + ")");
            }
        }

        if (phongConTrong.isEmpty()) {
            phongConTrong.add("Không có phòng trống");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, phongConTrong);
        dropdownPhongThue.setAdapter(adapter);
    }

    // --- HÀM TẢI DANH SÁCH NGƯỜI THUÊ TỪ SQLITE LÊN RECYCLERVIEW ---
    private void loadDanhSachNguoiThue() {
        listNguoiThue = db.getAllNguoiThue();
        nguoiThueAdapter = new NguoiThueAdapter(this, listNguoiThue, new NguoiThueAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ModelNguoiThue item) {
                selectedCccd = item.getCccd(); // Ghi nhớ CCCD của người đang chọn
                edtHoTen.setText(item.getHoTen());
                edtSoDienThoai.setText(item.getSoDienThoai());
                edtCCCD.setText(item.getCccd());
                edtNgaySinh.setText(item.getNgaySinh());
                dropdownPhongThue.setText(item.getPhongThue(), false);
                edtQueQuan.setText(item.getQueQuan());
                edtNgayChuyenVao.setText(item.getNgayChuyenVao());
                edtNgayChuyenDi.setText(item.getNgayChuyenDi());
            }
        });
        recyclerDanhSachNguoiThue.setLayoutManager(new LinearLayoutManager(this));
        recyclerDanhSachNguoiThue.setAdapter(nguoiThueAdapter);
    }

    // --- HÀM LÀM TRẮNG FORM ---
    private void lamMoiForm() {
        selectedCccd = ""; // Reset biến ghi nhớ
        edtHoTen.setText("");
        edtSoDienThoai.setText("");
        edtCCCD.setText("");
        edtNgaySinh.setText("");
        dropdownPhongThue.setText("", false);
        edtQueQuan.setText("");
        edtNgayChuyenVao.setText("");
        edtNgayChuyenDi.setText("");
        edtHoTen.requestFocus();
        loadDanhSachPhongTrong();
    }
}
