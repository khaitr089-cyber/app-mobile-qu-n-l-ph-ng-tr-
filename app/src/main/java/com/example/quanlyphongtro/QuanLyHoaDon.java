package com.example.quanlyphongtro;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class QuanLyHoaDon extends AppCompatActivity {

    // Khai báo các biến giao diện
    TextInputEditText txtMahoadon, txtMaphong, txtDiencu, txtDienmoi, txtGiadien;
    TextInputEditText txtNuoccu, txtNuocmoi, txtGianuoc, txtTienphong, txtPhidichvu;
    TextView txtTongtien;
    Button btnTinh, btnThem, btnXoa;
    ListView lvHoadon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giaodienhoadon);

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

        // 2. Sự kiện bấm nút "Tính tiền" áp dụng chính xác công thức của bạn
        btnTinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Lấy dữ liệu chỉ số điện
                    double dienCu = Double.parseDouble(txtDiencu.getText().toString());
                    double dienMoi = Double.parseDouble(txtDienmoi.getText().toString());
                    double giaDien = Double.parseDouble(txtGiadien.getText().toString());

                    // Lấy dữ liệu chỉ số nước
                    double nuocCu = Double.parseDouble(txtNuoccu.getText().toString());
                    double nuocMoi = Double.parseDouble(txtNuocmoi.getText().toString());
                    double giaNuoc = Double.parseDouble(txtGianuoc.getText().toString());

                    // Lấy tiền phòng và phí dịch vụ
                    double tienPhong = Double.parseDouble(txtTienphong.getText().toString());
                    double phiDichVu = Double.parseDouble(txtPhidichvu.getText().toString());

                    // === ÁP DỤNG CÔNG THỨC TÍNH TOÁN ===
                    double tienDien = (dienMoi - dienCu) * giaDien;
                    double tienNuoc = (nuocMoi - nuocCu) * giaNuoc;
                    double tongTien = tienPhong + tienDien + tienNuoc + phiDichVu;

                    // Hiển thị kết quả lên giao diện
                    txtTongtien.setText("Tổng tiền: " + tongTien + " VNĐ");

                } catch (NumberFormatException e) {
                    Toast.makeText(QuanLyHoaDon.this, "Vui lòng nhập đầy đủ các số liệu hợp lệ!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
