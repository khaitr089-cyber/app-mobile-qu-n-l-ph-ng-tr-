package com.example.quanlyphongtro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.quanlyphongtro.adapter.NguoiThueAdapter;
import com.example.quanlyphongtro.model.ModelNguoiThue;
import com.example.quanlyphongtro.model.ModelPhong;

public class QuanLyNguoiThue extends AppCompatActivity {

    // Khai báo các thành phần giao diện người thuê
    TextInputEditText edtHoTen, edtSoDienThoai, edtCCCD, edtNgaySinh, edtQueQuan, edtNgayChuyenVao, edtNgayChuyenDi, edtSoLuongNguoi, edtTimKiem;
    AutoCompleteTextView dropdownPhongThue;
    Button btnThemNguoiThue, btnSuaNguoiThue, btnXoaNguoiThue, btnLamMoiNguoiThue, btnQuayLaiNguoiThue;
    RecyclerView recyclerDanhSachNguoiThue;
    TextView tvTongSoNguoiThue;

    // Khai báo đối tượng SQLite và Adapter
    QuanLyCoSoDuLieu db;
    List<ModelNguoiThue> listNguoiThue;
    NguoiThueAdapter nguoiThueAdapter;

    private String selectedCccd = "";

    // Executor & Handler
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giaodiennguoithue);

        db = new QuanLyCoSoDuLieu(this);

        // --- BƯỚC 1: ÁNH XẠ CÁC VIEW ---
        edtHoTen = findViewById(R.id.edtHoTen);
        edtSoDienThoai = findViewById(R.id.edtSoDienThoai);
        edtCCCD = findViewById(R.id.edtCCCD);
        edtNgaySinh = findViewById(R.id.edtNgaySinh);
        dropdownPhongThue = findViewById(R.id.dropdownPhongThue);
        edtSoLuongNguoi = findViewById(R.id.edtSoLuongNguoi);
        edtQueQuan = findViewById(R.id.edtQueQuan);
        edtNgayChuyenVao = findViewById(R.id.edtNgayChuyenVao);
        edtNgayChuyenDi = findViewById(R.id.edtNgayChuyenDi);
        edtTimKiem = findViewById(R.id.edtTimKiem);

        btnThemNguoiThue = findViewById(R.id.btnThemNguoiThue);
        btnSuaNguoiThue = findViewById(R.id.btnSuaNguoiThue);
        btnXoaNguoiThue = findViewById(R.id.btnXoaNguoiThue);
        btnLamMoiNguoiThue = findViewById(R.id.btnLamMoiNguoiThue);
        btnQuayLaiNguoiThue = findViewById(R.id.btnQuayLaiNguoiThue);

        Button btnBaoCaoTamTru = findViewById(R.id.btnBaoCaoTamTru);
        if (btnBaoCaoTamTru != null) {
            btnBaoCaoTamTru.setOnClickListener(v -> hienThiDialogBaoCaoTamTru());
        }

        recyclerDanhSachNguoiThue = findViewById(R.id.recyclerDanhSachNguoiThue);
        tvTongSoNguoiThue = findViewById(R.id.tvTongSoNguoiThue);

        // --- BƯỚC 2: CHỌN LỊCH ---
        edtNgaySinh.setOnClickListener(v -> showDatePickerDialog(edtNgaySinh));
        edtNgayChuyenVao.setOnClickListener(v -> showDatePickerDialog(edtNgayChuyenVao));
        edtNgayChuyenDi.setOnClickListener(v -> showDatePickerDialog(edtNgayChuyenDi));

        loadDanhSachPhongTrong();
        loadDanhSachNguoiThue();

        // --- DEBOUNCE 250MS TÌM KIẾM ---
        if (edtTimKiem != null) {
            Handler debounceHandler = new Handler(Looper.getMainLooper());
            Runnable searchRunnable = () -> {
                String kw = edtTimKiem.getText() != null ? edtTimKiem.getText().toString().trim() : "";
                timKiemNguoiThue(kw);
            };

            edtTimKiem.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    debounceHandler.removeCallbacks(searchRunnable);
                    debounceHandler.postDelayed(searchRunnable, 250);
                }
                @Override public void afterTextChanged(Editable s) {}
            });
        }

        // --- BƯỚC 3: THÊM NGƯỜI THUÊ ---
        btnThemNguoiThue.setOnClickListener(v -> {
            String hoTen = edtHoTen.getText() != null ? edtHoTen.getText().toString().trim() : "";
            String sdt = edtSoDienThoai.getText() != null ? edtSoDienThoai.getText().toString().trim() : "";
            String cccd = edtCCCD.getText() != null ? edtCCCD.getText().toString().trim() : "";
            String ngaySinh = edtNgaySinh.getText() != null ? edtNgaySinh.getText().toString().trim() : "";
            String phongThue = dropdownPhongThue.getText().toString().trim();
            String soLuong = edtSoLuongNguoi.getText() != null ? edtSoLuongNguoi.getText().toString().trim() : "1";
            String queQuan = edtQueQuan.getText() != null ? edtQueQuan.getText().toString().trim() : "";
            String ngayVao = edtNgayChuyenVao.getText() != null ? edtNgayChuyenVao.getText().toString().trim() : "";
            String ngayDi = edtNgayChuyenDi.getText() != null ? edtNgayChuyenDi.getText().toString().trim() : "";

            if (soLuong.isEmpty()) soLuong = "1";

            if (hoTen.isEmpty() || sdt.isEmpty() || cccd.isEmpty() || phongThue.isEmpty() || queQuan.isEmpty() || phongThue.contains("Không có")) {
                Toast.makeText(QuanLyNguoiThue.this, "Vui lòng nhập đầy đủ thông tin hoặc chọn phòng trống!", Toast.LENGTH_SHORT).show();
            } else {
                String finalSoLuong = soLuong;
                executor.execute(() -> {
                    boolean check = db.themNguoiThue(hoTen, sdt, cccd, phongThue, queQuan, ngaySinh, ngayVao, ngayDi, finalSoLuong);
                    if (check) {
                        String maPhong = phongThue.contains(" (") ? phongThue.split(" \\(")[0].trim() : phongThue.trim();
                        db.updateTrangThaiPhong(maPhong, "Đang thuê");
                    }
                    mainHandler.post(() -> {
                        if (check) {
                            Toast.makeText(QuanLyNguoiThue.this, "Thêm người thuê thành công và đã cập nhật phòng!", Toast.LENGTH_SHORT).show();
                            loadDanhSachNguoiThue();
                            loadDanhSachPhongTrong();
                            lamMoiForm();
                        } else {
                            Toast.makeText(QuanLyNguoiThue.this, "Thêm thất bại (CCCD đã tồn tại)!", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            }
        });

        // --- BƯỚC 4: SỬA THÔNG TIN NGƯỜI THUÊ ---
        btnSuaNguoiThue.setOnClickListener(v -> {
            if (selectedCccd.isEmpty()) {
                Toast.makeText(QuanLyNguoiThue.this, "Vui lòng chọn người thuê cần sửa từ danh sách!", Toast.LENGTH_SHORT).show();
                return;
            }
            String hoTen = edtHoTen.getText() != null ? edtHoTen.getText().toString().trim() : "";
            String sdt = edtSoDienThoai.getText() != null ? edtSoDienThoai.getText().toString().trim() : "";
            String newCccd = edtCCCD.getText() != null ? edtCCCD.getText().toString().trim() : "";
            String ngaySinh = edtNgaySinh.getText() != null ? edtNgaySinh.getText().toString().trim() : "";
            String phongThue = dropdownPhongThue.getText().toString().trim();
            String soLuong = edtSoLuongNguoi.getText() != null ? edtSoLuongNguoi.getText().toString().trim() : "1";
            String queQuan = edtQueQuan.getText() != null ? edtQueQuan.getText().toString().trim() : "";
            String ngayVao = edtNgayChuyenVao.getText() != null ? edtNgayChuyenVao.getText().toString().trim() : "";
            String ngayDi = edtNgayChuyenDi.getText() != null ? edtNgayChuyenDi.getText().toString().trim() : "";

            if (soLuong.isEmpty()) soLuong = "1";
            String finalSoLuong = soLuong;

            executor.execute(() -> {
                boolean check = db.suaNguoiThue(selectedCccd, hoTen, sdt, newCccd, phongThue, queQuan, ngaySinh, ngayVao, ngayDi, finalSoLuong);
                mainHandler.post(() -> {
                    if (check) {
                        Toast.makeText(QuanLyNguoiThue.this, "Sửa thông tin thành công!", Toast.LENGTH_SHORT).show();
                        loadDanhSachNguoiThue();
                        lamMoiForm();
                    } else {
                        Toast.makeText(QuanLyNguoiThue.this, "Sửa thất bại!", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });

        // --- BƯỚC 5: XÓA NGƯỜI THUÊ ---
        btnXoaNguoiThue.setOnClickListener(v -> {
            if (selectedCccd.isEmpty()) {
                Toast.makeText(QuanLyNguoiThue.this, "Vui lòng chọn người thuê cần xóa từ danh sách!", Toast.LENGTH_SHORT).show();
                return;
            }
            String phongThueHienTai = dropdownPhongThue.getText().toString().trim();

            executor.execute(() -> {
                boolean check = db.xoaNguoiThue(selectedCccd);
                if (check && !phongThueHienTai.isEmpty() && !phongThueHienTai.contains("Không có")) {
                    String maPhong = phongThueHienTai.contains(" (") ? phongThueHienTai.split(" \\(")[0].trim() : phongThueHienTai.trim();
                    db.updateTrangThaiPhong(maPhong, "Còn trống");
                }
                mainHandler.post(() -> {
                    if (check) {
                        Toast.makeText(QuanLyNguoiThue.this, "Xóa người thuê thành công!", Toast.LENGTH_SHORT).show();
                        loadDanhSachNguoiThue();
                        loadDanhSachPhongTrong();
                        lamMoiForm();
                    } else {
                        Toast.makeText(QuanLyNguoiThue.this, "Xóa thất bại!", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });

        // --- 6. LÀM MỚI FORM & QUAY LẠI ---
        btnLamMoiNguoiThue.setOnClickListener(v -> lamMoiForm());
        btnQuayLaiNguoiThue.setOnClickListener(v -> finish());
    }

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

    private void loadDanhSachPhongTrong() {
        executor.execute(() -> {
            List<ModelPhong> allPhong = db.getAllPhong();
            List<String> phongConTrong = new ArrayList<>();
            if (allPhong != null) {
                for (ModelPhong p : allPhong) {
                    if (p.getTrangThai() != null && p.getTrangThai().toLowerCase().contains("trống")) {
                        phongConTrong.add(p.getMaPhong() + " (" + p.getTenPhong() + ")");
                    }
                }
            }
            if (phongConTrong.isEmpty()) {
                phongConTrong.add("Không có phòng trống");
            }
            mainHandler.post(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, phongConTrong);
                dropdownPhongThue.setAdapter(adapter);
            });
        });
    }

    private void timKiemNguoiThue(String kw) {
        executor.execute(() -> {
            List<ModelNguoiThue> all = db.getAllNguoiThue();
            List<ModelNguoiThue> filtered = new ArrayList<>();
            if (all != null) {
                for (ModelNguoiThue nt : all) {
                    if (kw.isEmpty() || (nt.getHoTen() != null && nt.getHoTen().toLowerCase().contains(kw.toLowerCase())) ||
                        (nt.getSoDienThoai() != null && nt.getSoDienThoai().contains(kw))) {
                        filtered.add(nt);
                    }
                }
            }
            mainHandler.post(() -> hienThiDanhSachAdapter(filtered));
        });
    }

    private void loadDanhSachNguoiThue() {
        executor.execute(() -> {
            listNguoiThue = db.getAllNguoiThue();
            if (listNguoiThue == null) listNguoiThue = new ArrayList<>();
            mainHandler.post(() -> {
                int tongSoNguoi = listNguoiThue.size();
                if (tvTongSoNguoiThue != null) {
                    tvTongSoNguoiThue.setText("Tổng số hồ sơ người thuê: " + tongSoNguoi);
                }
                hienThiDanhSachAdapter(listNguoiThue);
            });
        });
    }

    private void hienThiDanhSachAdapter(List<ModelNguoiThue> list) {
        nguoiThueAdapter = new NguoiThueAdapter(this, list, item -> {
            selectedCccd = item.getCccd();
            edtHoTen.setText(item.getHoTen());
            edtSoDienThoai.setText(item.getSoDienThoai());
            edtCCCD.setText(item.getCccd());
            edtNgaySinh.setText(item.getNgaySinh());
            dropdownPhongThue.setText(item.getPhongThue(), false);
            edtSoLuongNguoi.setText(item.getSoLuongNguoi());
            edtQueQuan.setText(item.getQueQuan());
            edtNgayChuyenVao.setText(item.getNgayChuyenVao());
            edtNgayChuyenDi.setText(item.getNgayChuyenDi());
        });
        recyclerDanhSachNguoiThue.setLayoutManager(new LinearLayoutManager(this));
        recyclerDanhSachNguoiThue.setAdapter(nguoiThueAdapter);
    }

    private void lamMoiForm() {
        selectedCccd = "";
        if (edtHoTen != null) edtHoTen.setText("");
        if (edtSoDienThoai != null) edtSoDienThoai.setText("");
        if (edtCCCD != null) edtCCCD.setText("");
        if (edtNgaySinh != null) edtNgaySinh.setText("");
        if (dropdownPhongThue != null) dropdownPhongThue.setText("", false);
        if (edtSoLuongNguoi != null) edtSoLuongNguoi.setText("");
        if (edtQueQuan != null) edtQueQuan.setText("");
        if (edtNgayChuyenVao != null) edtNgayChuyenVao.setText("");
        if (edtNgayChuyenDi != null) edtNgayChuyenDi.setText("");
        if (edtHoTen != null) edtHoTen.requestFocus();
        loadDanhSachPhongTrong();
    }

    private void hienThiDialogBaoCaoTamTru() {
        executor.execute(() -> {
            List<ModelNguoiThue> list = db.getAllNguoiThue();
            mainHandler.post(() -> {
                if (list == null || list.isEmpty()) {
                    Toast.makeText(this, "Chưa có danh sách người thuê!", Toast.LENGTH_SHORT).show();
                    return;
                }

                StringBuilder sb = new StringBuilder();
                sb.append("DANH SÁCH BÁO CÁO KHAI BÁO TẠM TRÚ CÔNG AN PHƯỜNG/XÃ\n");
                sb.append("Ngày lập: ").append(new java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new java.util.Date())).append("\n\n");

                int stt = 1;
                for (ModelNguoiThue nt : list) {
                    sb.append(stt++).append(". ").append(nt.getHoTen()).append("\n")
                      .append("   • SĐT: ").append(nt.getSoDienThoai()).append("\n")
                      .append("   • CCCD: ").append(nt.getCccd()).append("\n")
                      .append("   • Quê quán: ").append(nt.getQueQuan()).append("\n")
                      .append("   • Phòng ở: ").append(nt.getPhongThue()).append("\n")
                      .append("   • Ngày chuyển vào: ").append(nt.getNgayChuyenVao()).append("\n\n");
                }

                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Khai Báo Tạm Trú Công An")
                        .setMessage(sb.toString())
                        .setPositiveButton("Gửi Zalo/SMS", (dialog, which) -> {
                            android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_SEND);
                            intent.setType("text/plain");
                            intent.putExtra(android.content.Intent.EXTRA_TEXT, sb.toString());
                            startActivity(android.content.Intent.createChooser(intent, "Gửi báo cáo tạm trú"));
                        })
                        .setNegativeButton("Đóng", null)
                        .show();
            });
        });
    }
}
