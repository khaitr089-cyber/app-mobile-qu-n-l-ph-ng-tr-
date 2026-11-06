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
import java.util.List;

public class QuanLyHoaDon extends AppCompatActivity {

    // Khai báo các biến giao diện
    TextInputEditText txtMahoadon, txtMaphong, txtDiencu, txtDienmoi, txtGiadien;
    TextInputEditText txtNuoccu, txtNuocmoi, txtGianuoc, txtTienphong, txtPhidichvu;
    TextView txtTongtien;
    Button btnTinh, btnThem, btnXoa;
    ListView lvHoadon;

    // Khai báo Database Helper
    QuanLyCoSoDuLieu dbHelper;
    double tongTienHienTai = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giaodienhoadon);

        // Khởi tạo Database Helper
        dbHelper = new QuanLyCoSoDuLieu(this);

        // 1. Ánh xạ các thành phần từ XML sang Java
        txtMahoadon = findViewById(R.id.txtmahoadon);
        txtMaphong = findViewById(R.id.txtmaphong);
        
        txtDiencu = findViewById(R.id.txtdiencu);
        txtDienmoi = findViewById(R.id.txtdienmoi);
        txtGiadien = findViewById(R.id.txtgiadien);
        
        txtNuoccu = findViewById(R.id.txtnuoccu);
        txtNuocmoi = findViewById(R.id.txtnuocmoi);
        txtGianuoc = findViewById(R.id.txtgianuoc);
        
        txtTienphong = findViewById(R.id.txttienphong);
        txtPhidichvu = findViewById(R.id.txtphidichvu);
        
        txtTongtien = findViewById(R.id.txttongtien);
        
        btnTinh = findViewById(R.id.btntinh);
        btnThem = findViewById(R.id.btnthem);
        btnXoa = findViewById(R.id.btnxoa);
        lvHoadon = findViewById(R.id.lvhoadon);

        // Load danh sách hóa đơn lên ListView ngay khi mở trang
        loadDanhSachHoaDon();

        // 2. Sự kiện nút "Tính tiền" (Áp dụng đúng công thức)
        btnTinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tinhTienHoaDon();
            }
        });

        // 3. Sự kiện nút "Lưu" (Thêm hóa đơn vào SQLite)
        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String maHD = txtMahoadon.getText().toString().trim();
                String maP = txtMaphong.getText().toString().trim();

                if (maHD.isEmpty() || maP.isEmpty()) {
                    Toast.makeText(QuanLyHoaDon.this, "Vui lòng nhập mã hóa đơn và mã phòng!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Gọi hàm lưu vào CSDL
                boolean ketQua = dbHelper.themHoaDon(maHD, maP, tongTienHienTai);
                if (ketQua) {
                    Toast.makeText(QuanLyHoaDon.this, "Lưu hóa đơn thành công!", Toast.LENGTH_SHORT).show();
                    loadDanhSachHoaDon();
                    xoaTrangForm();
                } else {
                    Toast.makeText(QuanLyHoaDon.this, "Lưu thất bại! Mã hóa đơn có thể bị trùng.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 4. Sự kiện nút "Xóa" (Làm mới form nhập liệu)
        btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xoaTrangForm();
            }
        });
    }

    // --- HÀM TÍNH TIỀN THEO CÔNG THỨC ---
    private void tinhTienHoaDon() {
        try {
            double dienCu = Double.parseDouble(txtDiencu.getText().toString());
            double dienMoi = Double.parseDouble(txtDienmoi.getText().toString());
            double giaDien = Double.parseDouble(txtGiadien.getText().toString());

            double nuocCu = Double.parseDouble(txtNuoccu.getText().toString());
            double nuocMoi = Double.parseDouble(txtNuocmoi.getText().toString());
            double giaNuoc = Double.parseDouble(txtGianuoc.getText().toString());

            double tienPhong = Double.parseDouble(txtTienphong.getText().toString());
            double phiDichVu = Double.parseDouble(txtPhidichvu.getText().toString());

            // --- ÁP DỤNG CÔNG THỨC ---
            double tienDien = (dienMoi - dienCu) * giaDien;
            double tienNuoc = (nuocMoi - nuocCu) * giaNuoc;
            tongTienHienTai = tienPhong + tienDien + tienNuoc + phiDichVu;

            txtTongtien.setText("Tổng tiền: " + tongTienHienTai + " VNĐ");

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ và chính xác các con số!", Toast.LENGTH_SHORT).show();
        }
    }

    // --- HÀM LOAD DANH SÁCH HÓA ĐƠN ---
    private void loadDanhSachHoaDon() {
        List<String> danhSach = dbHelper.getAllHoaDonString();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, danhSach);
        lvHoadon.setAdapter(adapter);
    }

    // --- HÀM LÀM MỚI FORM ---
    private void xoaTrangForm() {
        txtMahoadon.setText("");
        txtMaphong.setText("");
        txtDiencu.setText("");
        txtDienmoi.setText("");
        txtGiadien.setText("");
        txtNuoccu.setText("");
        txtNuocmoi.setText("");
        txtGianuoc.setText("");
        txtTienphong.setText("");
        txtPhidichvu.setText("");
        txtTongtien.setText("Tổng tiền: 0 VNĐ");
        tongTienHienTai = 0;
        txtMahoadon.requestFocus();
    }
}
