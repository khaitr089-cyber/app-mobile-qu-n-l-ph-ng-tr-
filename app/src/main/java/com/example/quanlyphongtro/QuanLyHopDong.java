package com.example.quanlyphongtro;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.example.quanlyphongtro.model.ModelHopDong;
import com.example.quanlyphongtro.model.ModelNguoiThue;
import com.example.quanlyphongtro.model.ModelPhong;

public class QuanLyHopDong extends AppCompatActivity {

    // Khai báo các view
    TextInputEditText txtTenchunha, txtSdtchunha, txtCccdchunha;
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

        // 1. Ánh xạ View BÊN A (BÊN CHO THUÊ)
        txtTenchunha = findViewById(R.id.txttenchunha);
        txtSdtchunha = findViewById(R.id.txtsdtchunha);
        txtCccdchunha = findViewById(R.id.txtcccdchunha);

        // 2. Ánh xạ View BÊN B (BÊN THUÊ) & Chi tiết hợp đồng
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

        // Nạp dữ liệu vào Spinner và Auto điền thông tin mặc định
        loadSpinnerData();
        autoDienThongTinMacDinh();
        loadDanhSachHopDong();

        // --- TÍNH NĂNG THÔNG MINH: CHỌN NGƯỜI THUÊ -> AUTO ĐIỀN PHÒNG & NGÀY BẮT ĐẦU / KẾT THÚC ---
        spnNguoidaidien.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedNguoiThue = parent.getItemAtPosition(position).toString();
                if (!selectedNguoiThue.contains("Không có")) {
                    String sdt = selectedNguoiThue.contains("-") ? selectedNguoiThue.split("-")[1].trim() : "";
                    
                    List<ModelNguoiThue> allNguoiThue = dbHelper.getAllNguoiThue();
                    for (ModelNguoiThue nt : allNguoiThue) {
                        if (nt.getSoDienThoai().equals(sdt) || selectedNguoiThue.contains(nt.getHoTen())) {
                            // 1. Tự động chọn đúng phòng mà người này đang thuê
                            String phongThueCuaHo = nt.getPhongThue();
                            if (phongThueCuaHo != null && !phongThueCuaHo.isEmpty()) {
                                chonItemSpinnerTheoGiaTri(spnPhongthue, phongThueCuaHo);
                            }

                            // 2. Tự động điền ngày bắt đầu bằng ngày chuyển vào
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                            if (nt.getNgayChuyenVao() != null && !nt.getNgayChuyenVao().isEmpty()) {
                                txtNgaybatdau.setText(nt.getNgayChuyenVao());
                                txtNgaythanhtoandautien.setText(nt.getNgayChuyenVao());
                            } else {
                                String today = sdf.format(new Date());
                                txtNgaybatdau.setText(today);
                                txtNgaythanhtoandautien.setText(today);
                            }

                            // 3. Tự động điền ngày kết thúc (lấy Ngày chuyển đi nếu có, nếu chưa có thì lấy 1 năm sau ngày bắt đầu)
                            if (nt.getNgayChuyenDi() != null && !nt.getNgayChuyenDi().isEmpty()) {
                                txtNgayketthuc.setText(nt.getNgayChuyenDi());
                            } else {
                                try {
                                    Date startDate = sdf.parse(txtNgaybatdau.getText().toString());
                                    Calendar cal = Calendar.getInstance();
                                    if (startDate != null) cal.setTime(startDate);
                                    cal.add(Calendar.YEAR, 1); // Cộng 1 năm làm thời hạn mặc định
                                    txtNgayketthuc.setText(sdf.format(cal.getTime()));
                                } catch (Exception e) {
                                    txtNgayketthuc.setText("");
                                }
                            }

                            break;
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Sự kiện khi chọn phòng thủ công -> Tự động điền giá thuê & tiền cọc
        spnPhongthue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                if (!selected.contains("Không có")) {
                    String maPhong = selected.split(" ")[0].trim();
                    int giaThuePhong = dbHelper.getGiaThuePhong(maPhong);
                    txtGiathue.setText(String.valueOf(giaThuePhong));
                    txtTiencoc.setText(String.valueOf(giaThuePhong)); // Mặc định cọc 1 tháng
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

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
                    Toast.makeText(QuanLyHopDong.this, "Cần có ít nhất 1 phòng và 1 người thuê trong hệ thống!", Toast.LENGTH_SHORT).show();
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
                    autoDienThongTinMacDinh();
                } else {
                    Toast.makeText(QuanLyHopDong.this, "Tạo thất bại! Mã hợp đồng có thể bị trùng.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // --- TÍNH NĂNG XEM CHI TIẾT HỢP ĐỒNG MẪU (F19) ---
        lvHopdong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<ModelHopDong> hopDongList = dbHelper.getAllHopDong();
                if (position >= 0 && position < hopDongList.size()) {
                    ModelHopDong hd = hopDongList.get(position);

                    String tenChuNha = txtTenchunha.getText().toString();
                    String sdtChuNha = txtSdtchunha.getText().toString();
                    String cccdChuNha = txtCccdchunha.getText().toString();

                    String chiTietMau = 
                            "CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM\n" +
                            "   Độc lập - Tự do - Hạnh phúc\n\n" +
                            "  HỢP ĐỒNG THUÊ PHÒNG TRỌ\n" +
                            "  (Số HĐ: " + hd.getMaHopDong() + ")\n\n" +
                            "BÊN A (BÊN CHO THUÊ):\n" +
                            "• Họ và tên: " + tenChuNha + "\n" +
                            "• Điện thoại: " + sdtChuNha + "\n" +
                            "• Số CCCD: " + cccdChuNha + "\n\n" +
                            "BÊN B (BÊN THUÊ):\n" +
                            "• Người đại diện: " + hd.getNguoiDaiDien() + "\n" +
                            "• Người ở cùng: " + hd.getNguoiOCung() + "\n\n" +
                            "ĐIỀU 1: THỎA THUẬN THUÊ\n" +
                            "• Phòng thuê số: " + hd.getMaPhong() + "\n" +
                            "• Thời hạn: Từ " + hd.getNgayBatDau() + " đến " + hd.getNgayKetThuc() + "\n\n" +
                            "ĐIỀU 2: GIÁ THUÊ VÀ ĐẶT CỌC\n" +
                            "• Giá thuê: " + hd.getGiaThue() + " VNĐ/tháng\n" +
                            "• Tiền đặt cọc: " + hd.getTienCoc() + " VNĐ\n" +
                            "• Ngày đóng tiền: Mùng " + hd.getNgayThanhToanHangThang() + " hàng tháng\n\n" +
                            "ĐIỀU 3: GHI CHÚ / ĐIỀU KHOẢN KHÁC\n" +
                            "• " + hd.getGhiChu() + "\n\n" +
                            "Trạng thái HĐ: " + hd.getTrangThai();

                    new AlertDialog.Builder(QuanLyHopDong.this)
                            .setTitle("Mẫu Hợp Đồng Chi Tiết")
                            .setMessage(chiTietMau)
                            .setPositiveButton("Đóng", null)
                            .show();
                }
            }
        });
    }

    private void chonItemSpinnerTheoGiaTri(Spinner spinner, String giaTri) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        if (adapter == null || giaTri == null) return;

        String maPhongCore = giaTri.split(" ")[0].trim();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().startsWith(maPhongCore)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void autoDienThongTinMacDinh() {
        String autoMaHD = "HD_" + System.currentTimeMillis();
        txtMahopdong.setText(autoMaHD);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        String ngayHienTai = sdf.format(cal.getTime());
        txtNgaybatdau.setText(ngayHienTai);
        txtNgaythanhtoandautien.setText(ngayHienTai);

        // Mặc định tự động điền ngày kết thúc là 1 năm sau
        cal.add(Calendar.YEAR, 1);
        txtNgayketthuc.setText(sdf.format(cal.getTime()));

        txtNgaythanhtoanhangthang.setText("5");
        txtNguoiocung.setText("Không");
    }

    private void loadSpinnerData() {
        List<ModelPhong> allPhong = dbHelper.getAllPhong();
        List<String> danhSachPhong = new ArrayList<>();
        for (ModelPhong p : allPhong) {
            danhSachPhong.add(p.getMaPhong() + " (" + p.getTenPhong() + " - " + p.getTrangThai() + ")");
        }
        if (danhSachPhong.isEmpty()) danhSachPhong.add("Không có phòng nào");
        ArrayAdapter<String> adapterPhong = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, danhSachPhong);
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
}
