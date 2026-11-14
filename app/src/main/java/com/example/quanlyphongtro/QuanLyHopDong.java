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
    Button btnTaohopdong, btnQuayLaiHopDong;
    ListView lvHopdong;

    QuanLyCoSoDuLieu dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
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
            btnQuayLaiHopDong = findViewById(R.id.btnQuayLaiHopDong);
            lvHopdong = findViewById(R.id.lvhopdong);

            if (btnQuayLaiHopDong != null) {
                btnQuayLaiHopDong.setOnClickListener(v -> finish());
            }

            // Nạp dữ liệu vào Spinner và Auto điền thông tin mặc định
            loadSpinnerData();
            autoDienThongTinMacDinh();
            loadDanhSachHopDong();

            // --- TÍNH NĂNG THÔNG MINH: CHỌN NGƯỜI THUÊ -> AUTO ĐIỀN PHÒNG & NGÀY BẮT ĐẦU / KẾT THÚC ---
            if (spnNguoidaidien != null) {
                spnNguoidaidien.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            if (parent.getItemAtPosition(position) == null) return;
                            String selectedNguoiThue = parent.getItemAtPosition(position).toString();
                            if (!selectedNguoiThue.contains("Không có")) {
                                String sdt = selectedNguoiThue.contains("-") ? selectedNguoiThue.split("-")[1].trim() : "";

                                List<ModelNguoiThue> allNguoiThue = dbHelper.getAllNguoiThue();
                                if (allNguoiThue != null) {
                                    for (ModelNguoiThue nt : allNguoiThue) {
                                        if (nt != null) {
                                            String ntSdt = nt.getSoDienThoai() != null ? nt.getSoDienThoai() : "";
                                            String ntTen = nt.getHoTen() != null ? nt.getHoTen() : "";
                                            if ((!sdt.isEmpty() && ntSdt.equals(sdt)) || (!ntTen.isEmpty() && selectedNguoiThue.contains(ntTen))) {
                                                String phongThueCuaHo = nt.getPhongThue();
                                                if (phongThueCuaHo != null && !phongThueCuaHo.isEmpty() && spnPhongthue != null) {
                                                    chonItemSpinnerTheoGiaTri(spnPhongthue, phongThueCuaHo);
                                                }

                                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                                if (nt.getNgayChuyenVao() != null && !nt.getNgayChuyenVao().isEmpty()) {
                                                    if (txtNgaybatdau != null) txtNgaybatdau.setText(nt.getNgayChuyenVao());
                                                    if (txtNgaythanhtoandautien != null) txtNgaythanhtoandautien.setText(nt.getNgayChuyenVao());
                                                } else {
                                                    String today = sdf.format(new Date());
                                                    if (txtNgaybatdau != null) txtNgaybatdau.setText(today);
                                                    if (txtNgaythanhtoandautien != null) txtNgaythanhtoandautien.setText(today);
                                                }

                                                if (nt.getNgayChuyenDi() != null && !nt.getNgayChuyenDi().isEmpty()) {
                                                    if (txtNgayketthuc != null) txtNgayketthuc.setText(nt.getNgayChuyenDi());
                                                } else {
                                                    try {
                                                        String bdStr = txtNgaybatdau != null && txtNgaybatdau.getText() != null ? txtNgaybatdau.getText().toString() : "";
                                                        Date startDate = sdf.parse(bdStr);
                                                        Calendar cal = Calendar.getInstance();
                                                        if (startDate != null) cal.setTime(startDate);
                                                        cal.add(Calendar.YEAR, 1);
                                                        if (txtNgayketthuc != null) txtNgayketthuc.setText(sdf.format(cal.getTime()));
                                                    } catch (Exception e) {
                                                        if (txtNgayketthuc != null) txtNgayketthuc.setText("");
                                                    }
                                                }
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (Exception ignored) {}
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
            }

            // Sự kiện khi chọn phòng thủ công -> Tự động điền giá thuê & tiền cọc
            if (spnPhongthue != null) {
                spnPhongthue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            if (parent.getItemAtPosition(position) == null) return;
                            String selected = parent.getItemAtPosition(position).toString();
                            if (!selected.contains("Không có")) {
                                String maPhong = selected.split(" ")[0].trim();
                                int giaThuePhong = dbHelper.getGiaThuePhong(maPhong);
                                if (txtGiathue != null) txtGiathue.setText(String.valueOf(giaThuePhong));
                                if (txtTiencoc != null) txtTiencoc.setText(String.valueOf(giaThuePhong));
                            }
                        } catch (Exception ignored) {}
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
            }

            // Sự kiện Lưu hợp đồng
            if (btnTaohopdong != null) {
                btnTaohopdong.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            String maHD = txtMahopdong != null && txtMahopdong.getText() != null ? txtMahopdong.getText().toString().trim() : "";
                            String nguoiOCung = txtNguoiocung != null && txtNguoiocung.getText() != null ? txtNguoiocung.getText().toString().trim() : "";
                            String ngayBD = txtNgaybatdau != null && txtNgaybatdau.getText() != null ? txtNgaybatdau.getText().toString().trim() : "";
                            String ngayKT = txtNgayketthuc != null && txtNgayketthuc.getText() != null ? txtNgayketthuc.getText().toString().trim() : "";
                            String giaStr = txtGiathue != null && txtGiathue.getText() != null ? txtGiathue.getText().toString().trim() : "";
                            String cocStr = txtTiencoc != null && txtTiencoc.getText() != null ? txtTiencoc.getText().toString().trim() : "";
                            String ngayTTThang = txtNgaythanhtoanhangthang != null && txtNgaythanhtoanhangthang.getText() != null ? txtNgaythanhtoanhangthang.getText().toString().trim() : "";
                            String ngayTTDau = txtNgaythanhtoandautien != null && txtNgaythanhtoandautien.getText() != null ? txtNgaythanhtoandautien.getText().toString().trim() : "";
                            String ghiChu = txtGhichu != null && txtGhichu.getText() != null ? txtGhichu.getText().toString().trim() : "";

                            if (maHD.isEmpty() || ngayBD.isEmpty() || giaStr.isEmpty() || cocStr.isEmpty()) {
                                Toast.makeText(QuanLyHopDong.this, "Vui lòng nhập đầy đủ thông tin bắt buộc!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            String phongChon = (spnPhongthue != null && spnPhongthue.getSelectedItem() != null) ? spnPhongthue.getSelectedItem().toString() : "";
                            String nguoiChon = (spnNguoidaidien != null && spnNguoidaidien.getSelectedItem() != null) ? spnNguoidaidien.getSelectedItem().toString() : "";

                            if (phongChon.contains("Không có") || nguoiChon.contains("Không có")) {
                                Toast.makeText(QuanLyHopDong.this, "Cần có ít nhất 1 phòng và 1 người thuê trong hệ thống!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            String maPhong = phongChon.split(" ")[0].trim();
                            if (dbHelper.kiemtraTrungLichHopDong(maPhong, ngayBD, ngayKT)) {
                                Toast.makeText(QuanLyHopDong.this, "Lỗi: Phòng này đã có hợp đồng hiệu lực trong khoảng thời gian này!", Toast.LENGTH_LONG).show();
                                return;
                            }
                            double giaThue = 0;
                            double tienCoc = 0;
                            try {
                                giaThue = Double.parseDouble(giaStr.replaceAll("[^0-9.]", ""));
                                tienCoc = Double.parseDouble(cocStr.replaceAll("[^0-9.]", ""));
                            } catch (Exception e) {
                                giaThue = 0;
                                tienCoc = 0;
                            }

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
                        } catch (Exception e) {
                            Toast.makeText(QuanLyHopDong.this, "Lỗi khi lưu hợp đồng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            // --- TÍNH NĂNG XEM CHI TIẾT HỢP ĐỒNG MẪU ---
            if (lvHopdong != null) {
                lvHopdong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            List<ModelHopDong> hopDongList = dbHelper.getAllHopDong();
                            if (hopDongList != null && position >= 0 && position < hopDongList.size()) {
                                ModelHopDong hd = hopDongList.get(position);
                                if (hd == null) return;

                                String tenChuNha = txtTenchunha != null && txtTenchunha.getText() != null ? txtTenchunha.getText().toString() : "";
                                String sdtChuNha = txtSdtchunha != null && txtSdtchunha.getText() != null ? txtSdtchunha.getText().toString() : "";
                                String cccdChuNha = txtCccdchunha != null && txtCccdchunha.getText() != null ? txtCccdchunha.getText().toString() : "";

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
                        } catch (Exception ignored) {}
                    }
                });
            }
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi mở màn hình Hợp đồng: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void chonItemSpinnerTheoGiaTri(Spinner spinner, String giaTri) {
        try {
            if (spinner == null || spinner.getAdapter() == null || giaTri == null) return;
            ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
            if (adapter == null) return;

            String maPhongCore = giaTri.split(" ")[0].trim();
            for (int i = 0; i < adapter.getCount(); i++) {
                Object item = adapter.getItem(i);
                if (item != null && item.toString().startsWith(maPhongCore)) {
                    spinner.setSelection(i);
                    break;
                }
            }
        } catch (Exception ignored) {}
    }

    private void autoDienThongTinMacDinh() {
        try {
            String autoMaHD = "HD_" + System.currentTimeMillis();
            if (txtMahopdong != null) txtMahopdong.setText(autoMaHD);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Calendar cal = Calendar.getInstance();
            String ngayHienTai = sdf.format(cal.getTime());
            if (txtNgaybatdau != null) txtNgaybatdau.setText(ngayHienTai);
            if (txtNgaythanhtoandautien != null) txtNgaythanhtoandautien.setText(ngayHienTai);

            cal.add(Calendar.YEAR, 1);
            if (txtNgayketthuc != null) txtNgayketthuc.setText(sdf.format(cal.getTime()));

            if (txtNgaythanhtoanhangthang != null) txtNgaythanhtoanhangthang.setText("5");
            if (txtNguoiocung != null) txtNguoiocung.setText("Không");
        } catch (Exception ignored) {}
    }

    private void loadSpinnerData() {
        try {
            if (dbHelper == null) return;

            List<ModelPhong> allPhong = dbHelper.getAllPhong();
            List<String> danhSachPhong = new ArrayList<>();
            if (allPhong != null) {
                for (ModelPhong p : allPhong) {
                    if (p != null) {
                        danhSachPhong.add(p.getMaPhong() + " (" + p.getTenPhong() + " - " + p.getTrangThai() + ")");
                    }
                }
            }
            if (danhSachPhong.isEmpty()) danhSachPhong.add("Không có phòng nào");
            ArrayAdapter<String> adapterPhong = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, danhSachPhong);
            adapterPhong.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            if (spnPhongthue != null) spnPhongthue.setAdapter(adapterPhong);

            List<ModelNguoiThue> allNguoiThue = dbHelper.getAllNguoiThue();
            List<String> tenNguoiThue = new ArrayList<>();
            if (allNguoiThue != null) {
                for (ModelNguoiThue nt : allNguoiThue) {
                    if (nt != null) {
                        tenNguoiThue.add(nt.getHoTen() + " - " + nt.getSoDienThoai());
                    }
                }
            }
            if (tenNguoiThue.isEmpty()) tenNguoiThue.add("Không có người thuê nào");
            ArrayAdapter<String> adapterNguoiThue = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tenNguoiThue);
            adapterNguoiThue.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            if (spnNguoidaidien != null) spnNguoidaidien.setAdapter(adapterNguoiThue);
        } catch (Exception ignored) {}
    }

    private void loadDanhSachHopDong() {
        try {
            if (dbHelper == null || lvHopdong == null) return;
            List<String> list = dbHelper.getAllHopDongString();
            if (list == null) list = new ArrayList<>();
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
            lvHopdong.setAdapter(adapter);
        } catch (Exception ignored) {}
    }
}
