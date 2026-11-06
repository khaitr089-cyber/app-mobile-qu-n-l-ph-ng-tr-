package com.example.quanlyphongtro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

import com.example.quanlyphongtro.adapter.NguoiThueAdapter;
import com.example.quanlyphongtro.model.ModelNguoiThue;
import com.example.quanlyphongtro.model.ModelPhong;

public class QuanLyNguoiThue extends AppCompatActivity {

    // Khai báo các thành phần giao diện người thuê
    TextInputEditText edtHoTen, edtSoDienThoai, edtCCCD, edtQueQuan;
    AutoCompleteTextView dropdownPhongThue;
    Button btnThemNguoiThue, btnSuaNguoiThue, btnXoaNguoiThue, btnLamMoiNguoiThue, btnQuayLaiNguoiThue;
    RecyclerView recyclerDanhSachNguoiThue;

    // Khai báo đối tượng SQLite và Adapter
    QuanLyCoSoDuLieu db;
    List<ModelNguoiThue> listNguoiThue;
    NguoiThueAdapter nguoiThueAdapter;

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
        dropdownPhongThue = findViewById(R.id.dropdownPhongThue);
        edtQueQuan = findViewById(R.id.edtQueQuan);

        btnThemNguoiThue = findViewById(R.id.btnThemNguoiThue);
        btnSuaNguoiThue = findViewById(R.id.btnSuaNguoiThue);
        btnXoaNguoiThue = findViewById(R.id.btnXoaNguoiThue);
        btnLamMoiNguoiThue = findViewById(R.id.btnLamMoiNguoiThue);
        btnQuayLaiNguoiThue = findViewById(R.id.btnQuayLaiNguoiThue);
        recyclerDanhSachNguoiThue = findViewById(R.id.recyclerDanhSachNguoiThue);

        // Nạp danh sách phòng còn trống vào Dropdown chọn phòng
        loadDanhSachPhongTrong();

        // Tải danh sách người thuê từ SQLite lên màn hình
        loadDanhSachNguoiThue();

        // --- BƯỚC 2: CHỨC NĂNG THÊM NGƯỜI THUÊ ---
        btnThemNguoiThue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hoTen = edtHoTen.getText().toString().trim();
                String sdt = edtSoDienThoai.getText().toString().trim();
                String cccd = edtCCCD.getText().toString().trim();
                String phongThue = dropdownPhongThue.getText().toString().trim();
                String queQuan = edtQueQuan.getText().toString().trim();

                // Kiểm tra rỗng
                if (hoTen.isEmpty() || sdt.isEmpty() || cccd.isEmpty() || phongThue.isEmpty() || queQuan.isEmpty() || phongThue.contains("Không có")) {
                    Toast.makeText(QuanLyNguoiThue.this, "Vui lòng nhập đầy đủ thông tin hoặc chọn phòng trống!", Toast.LENGTH_SHORT).show();
                } else {
                    // Gọi hàm thêm người thuê vào SQLite
                    boolean check = db.themNguoiThue(hoTen, sdt, cccd, phongThue, queQuan);
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
                        Toast.makeText(QuanLyNguoiThue.this, "Thêm thất bại (CCCD có thể đã tồn tại)!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // --- BƯỚC 3: CHỨC NĂNG SỬA THÔNG TIN NGƯỜI THUÊ ---
        btnSuaNguoiThue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hoTen = edtHoTen.getText().toString().trim();
                String sdt = edtSoDienThoai.getText().toString().trim();
                String cccd = edtCCCD.getText().toString().trim();
                String phongThue = dropdownPhongThue.getText().toString().trim();
                String queQuan = edtQueQuan.getText().toString().trim();

                if (cccd.isEmpty()) {
                    Toast.makeText(QuanLyNguoiThue.this, "Vui lòng chọn hoặc nhập CCCD người cần sửa!", Toast.LENGTH_SHORT).show();
                } else {
                    // Gọi hàm sửa trong SQLite
                    boolean check = db.suaNguoiThue(hoTen, sdt, cccd, phongThue, queQuan);
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

        // --- BƯỚC 4: CHỨC NĂNG XÓA NGƯỜI THUÊ ---
        btnXoaNguoiThue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cccd = edtCCCD.getText().toString().trim();
                // Lấy thông tin phòng thuê cũ để khi xóa có thể trả trạng thái phòng về "Còn trống" nếu muốn
                String phongThueHienTai = dropdownPhongThue.getText().toString().trim();

                if (cccd.isEmpty()) {
                    Toast.makeText(QuanLyNguoiThue.this, "Vui lòng nhập hoặc chọn CCCD người cần xóa!", Toast.LENGTH_SHORT).show();
                } else {
                    // Gọi hàm xóa khỏi SQLite
                    boolean check = db.xoaNguoiThue(cccd);
                    if (check) {
                        // Nếu xóa người thuê, cập nhật lại trạng thái phòng đó thành "Còn trống"
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

        // --- BƯỚC 5: LÀM MỚI FORM ---
        btnLamMoiNguoiThue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lamMoiForm();
                Toast.makeText(QuanLyNguoiThue.this, "Đã làm mới form", Toast.LENGTH_SHORT).show();
            }
        });

        // --- BƯỚC 6: QUAY LẠI MÀN HÌNH CHÍNH ---
        btnQuayLaiNguoiThue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    // --- HÀM NẠP DANH SÁCH PHÒNG CÒN TRỐNG VÀO DROPDOWN ---
    private void loadDanhSachPhongTrong() {
        List<ModelPhong> allPhong = db.getAllPhong();
        List<String> phongConTrong = new ArrayList<>();

        for (ModelPhong p : allPhong) {
            // Lọc các phòng có trạng thái chứa chữ "trống" (ví dụ: "Còn trống")
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
                // Click vào item nào trên danh sách -> Đổ dữ liệu lên các ô EditText để sửa/xóa
                edtHoTen.setText(item.getHoTen());
                edtSoDienThoai.setText(item.getSoDienThoai());
                edtCCCD.setText(item.getCccd());
                edtCCCD.setEnabled(false); // Khóa không cho sửa số CCCD khi đang cập nhật
                dropdownPhongThue.setText(item.getPhongThue(), false);
                edtQueQuan.setText(item.getQueQuan());
            }
        });
        recyclerDanhSachNguoiThue.setLayoutManager(new LinearLayoutManager(this));
        recyclerDanhSachNguoiThue.setAdapter(nguoiThueAdapter);
    }

    // --- HÀM LÀM TRẮNG FORM ---
    private void lamMoiForm() {
        edtHoTen.setText("");
        edtSoDienThoai.setText("");
        edtCCCD.setText("");
        edtCCCD.setEnabled(true); // Cho phép nhập lại CCCD khi thêm mới
        dropdownPhongThue.setText("", false);
        edtQueQuan.setText("");
        edtHoTen.requestFocus();
        loadDanhSachPhongTrong();
    }
}
