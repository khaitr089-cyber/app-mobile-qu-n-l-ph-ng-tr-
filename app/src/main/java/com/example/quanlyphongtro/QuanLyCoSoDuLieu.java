package com.example.quanlyphongtro;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

import com.example.quanlyphongtro.model.ModelDienNuoc;
import com.example.quanlyphongtro.model.ModelHopDong;
import com.example.quanlyphongtro.model.ModelNguoiThue;
import com.example.quanlyphongtro.model.ModelPhong;
import com.example.quanlyphongtro.model.ModelTaiSan;
import com.example.quanlyphongtro.model.ModelThuChi;

public class QuanLyCoSoDuLieu extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "quanlyphongtro.db";
    private static final int DATABASE_VERSION = 17; // Tăng version lên 17 để cập nhật thông tin Chủ nhà Bên A và Lịch sử Thu Chi

    public QuanLyCoSoDuLieu(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Tạo bảng Người Dùng (Bổ sung Họ tên Chủ nhà, SĐT, CCCD)
        String sqlUser = "CREATE TABLE NguoiDung (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "tenDangNhap TEXT UNIQUE, " +
                "matKhau TEXT, " +
                "hoTenChuNha TEXT, " +
                "sdtChuNha TEXT, " +
                "cccdChuNha TEXT)";
        db.execSQL(sqlUser);
        db.execSQL("INSERT INTO NguoiDung(tenDangNhap, matKhau, hoTenChuNha, sdtChuNha, cccdChuNha) VALUES('admin', '123456', 'Nguyễn Văn A', '0987654321', '001090123456')");

        // 2. Tạo bảng Phòng Trọ
        String sqlPhong = "CREATE TABLE PhongTro (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "maPhong TEXT, " +
                "tenPhong TEXT, " +
                "giaThue INTEGER, " +
                "trangThai TEXT, " +
                "tang TEXT, " +
                "dienTich TEXT, " +
                "sucChua TEXT, " +
                "ghiChuThietBi TEXT, " +
                "username TEXT)";
        db.execSQL(sqlPhong);

        // 3. Tạo bảng Người Thuê
        String sqlNguoiThue = "CREATE TABLE NguoiThue (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "hoTen TEXT, " +
                "soDienThoai TEXT, " +
                "cccd TEXT, " +
                "phongThue TEXT, " +
                "queQuan TEXT, " +
                "ngaySinh TEXT, " +
                "gioiTinh TEXT, " +
                "ngayChuyenVao TEXT, " +
                "ngayChuyenDi TEXT, " +
                "soLuongNguoi TEXT, " +
                "username TEXT)";
        db.execSQL(sqlNguoiThue);

        // 4. Tạo bảng Hóa Đơn
        String sqlHoaDon = "CREATE TABLE HoaDon (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "maHoaDon TEXT UNIQUE, " +
                "maPhong TEXT, " +
                "tongTien REAL, " +
                "daThanhToan REAL, " +
                "congNo REAL, " +
                "phuThu REAL, " +
                "giamTru REAL, " +
                "phuongThucTT TEXT, " +
                "hanThanhToan TEXT, " +
                "trangThai TEXT, " +
                "lyDoHuy TEXT, " +
                "username TEXT)";
        db.execSQL(sqlHoaDon);

        // 5. Tạo bảng Hợp Đồng
        String sqlHopDong = "CREATE TABLE HopDong (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "maHopDong TEXT UNIQUE, " +
                "maPhong TEXT, " +
                "nguoiDaiDien TEXT, " +
                "nguoiOCung TEXT, " +
                "ngayBatDau TEXT, " +
                "ngayKetThuc TEXT, " +
                "giaThue REAL, " +
                "tienCoc REAL, " +
                "ngayThanhToanHangThang TEXT, " +
                "ngayThanhToanDauTien TEXT, " +
                "ghiChu TEXT, " +
                "trangThai TEXT, " +
                "username TEXT)";
        db.execSQL(sqlHopDong);

        // 6. Tạo bảng Điện Nước Dịch Vụ
        String sqlDienNuoc = "CREATE TABLE DienNuoc (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "maPhong TEXT, " +
                "thangGhi TEXT, " +
                "dienCu REAL, " +
                "dienMoi REAL, " +
                "nuocCu REAL, " +
                "nuocMoi REAL, " +
                "tienDichVu REAL, " +
                "tongTien REAL, " +
                "username TEXT)";
        db.execSQL(sqlDienNuoc);

        // 7. Tạo bảng Sổ Thu Chi & Bảo Trì
        String sqlThuChi = "CREATE TABLE ThuChi (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "loai TEXT, " +
                "soTien REAL, " +
                "danhMuc TEXT, " +
                "ghiChu TEXT, " +
                "ngayGhi TEXT, " +
                "username TEXT)";
        db.execSQL(sqlThuChi);

        // 8. Tạo bảng Tài Sản & Thiết Bị Phòng
        String sqlTaiSan = "CREATE TABLE TaiSan (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "maPhong TEXT, " +
                "tenTaiSan TEXT, " +
                "trangThai TEXT, " +
                "giaTri REAL, " +
                "username TEXT)";
        db.execSQL(sqlTaiSan);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS NguoiDung");
        db.execSQL("DROP TABLE IF EXISTS PhongTro");
        db.execSQL("DROP TABLE IF EXISTS NguoiThue");
        db.execSQL("DROP TABLE IF EXISTS HoaDon");
        db.execSQL("DROP TABLE IF EXISTS HopDong");
        db.execSQL("DROP TABLE IF EXISTS DienNuoc");
        db.execSQL("DROP TABLE IF EXISTS ThuChi");
        db.execSQL("DROP TABLE IF EXISTS TaiSan");
        onCreate(db);
    }

    // --- PHÂN HỆ 1: TÀI KHOẢN & THÔNG TIN CHỦ NHÀ ---
    public boolean dangnhap(String taiKhoan, String matKhau) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM NguoiDung WHERE tenDangNhap=? AND matKhau=?",
                new String[]{taiKhoan, matKhau}
        );
        boolean ketQua = cursor.moveToFirst();
        cursor.close();
        db.close();
        return ketQua;
    }

    public boolean dangkyFull(String taiKhoan, String matKhau, String hoTen, String sdt, String cccd) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tenDangNhap", taiKhoan);
        values.put("matKhau", matKhau);
        values.put("hoTenChuNha", hoTen);
        values.put("sdtChuNha", sdt);
        values.put("cccdChuNha", cccd);
        long result = db.insert("NguoiDung", null, values);
        db.close();
        return result != -1;
    }

    public String[] getThongTinChuNha(String username) {
        String[] info = new String[]{"Nguyễn Văn A", "0987654321", "001090123456"};
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT hoTenChuNha, sdtChuNha, cccdChuNha FROM NguoiDung WHERE tenDangNhap = ?", new String[]{username});
        if (cursor.moveToFirst()) {
            String hoTen = cursor.getString(0);
            String sdt = cursor.getString(1);
            String cccd = cursor.getString(2);
            if (hoTen != null && !hoTen.isEmpty()) info[0] = hoTen;
            if (sdt != null && !sdt.isEmpty()) info[1] = sdt;
            if (cccd != null && !cccd.isEmpty()) info[2] = cccd;
        }
        cursor.close();
        db.close();
        return info;
    }

    // --- PHÂN HỆ 2: QUẢN LÝ PHÒNG TRỌ ---
    public boolean checkMaPhongExist(String maPhong) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM PhongTro WHERE maPhong = ? AND username = ?", new String[]{maPhong, UserSession.currentUser});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    public boolean themPhong(String maPhong, String tenPhong, String giaThueStr, String trangThai, String tang, String dienTich) {
        if (checkMaPhongExist(maPhong)) {
            return false;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("maPhong", maPhong);
        values.put("tenPhong", tenPhong);
        try {
            int giaThue = Integer.parseInt(giaThueStr);
            values.put("giaThue", giaThue);
        } catch (NumberFormatException e) {
            values.put("giaThue", 0);
        }
        values.put("trangThai", trangThai);
        values.put("tang", tang);
        values.put("dienTich", dienTich);
        values.put("username", UserSession.currentUser);
        long result = db.insert("PhongTro", null, values);
        db.close();
        return result != -1;
    }

    public boolean suaPhong(String maPhong, String tenPhong, String giaThueStr, String trangThai, String tang, String dienTich) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tenPhong", tenPhong);
        try {
            int giaThue = Integer.parseInt(giaThueStr);
            values.put("giaThue", giaThue);
        } catch (NumberFormatException e) {
            values.put("giaThue", 0);
        }
        values.put("trangThai", trangThai);
        values.put("tang", tang);
        values.put("dienTich", dienTich);
        int rows = db.update("PhongTro", values, "maPhong = ? AND username = ?", new String[]{maPhong, UserSession.currentUser});
        db.close();
        return rows > 0;
    }

    public boolean updateTrangThaiPhong(String maPhong, String trangThaiMoi) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("trangThai", trangThaiMoi);
        int rows = db.update("PhongTro", values, "maPhong = ? AND username = ?", new String[]{maPhong, UserSession.currentUser});
        db.close();
        return rows > 0;
    }

    public boolean xoaPhong(String maPhong) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete("PhongTro", "maPhong = ? AND username = ?", new String[]{maPhong, UserSession.currentUser});
        db.close();
        return rows > 0;
    }

    public List<ModelPhong> getAllPhong() {
        List<ModelPhong> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM PhongTro WHERE username = ?", new String[]{UserSession.currentUser});
        if (cursor.moveToFirst()) {
            do {
                String ma = cursor.getString(cursor.getColumnIndexOrThrow("maPhong"));
                String ten = cursor.getString(cursor.getColumnIndexOrThrow("tenPhong"));
                int gia = cursor.getInt(cursor.getColumnIndexOrThrow("giaThue"));
                String tt = cursor.getString(cursor.getColumnIndexOrThrow("trangThai"));
                String tang = cursor.getString(cursor.getColumnIndexOrThrow("tang"));
                String dt = cursor.getString(cursor.getColumnIndexOrThrow("dienTich"));
                list.add(new ModelPhong(ma, ten, String.valueOf(gia), tt, tang, dt));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public List<ModelPhong> getPhongByTrangThai(String trangThaiLoc) {
        List<ModelPhong> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM PhongTro WHERE username = ? AND trangThai LIKE ?", new String[]{UserSession.currentUser, "%" + trangThaiLoc + "%"});
        if (cursor.moveToFirst()) {
            do {
                String ma = cursor.getString(cursor.getColumnIndexOrThrow("maPhong"));
                String ten = cursor.getString(cursor.getColumnIndexOrThrow("tenPhong"));
                int gia = cursor.getInt(cursor.getColumnIndexOrThrow("giaThue"));
                String tt = cursor.getString(cursor.getColumnIndexOrThrow("trangThai"));
                String tang = cursor.getString(cursor.getColumnIndexOrThrow("tang"));
                String dt = cursor.getString(cursor.getColumnIndexOrThrow("dienTich"));
                list.add(new ModelPhong(ma, ten, String.valueOf(gia), tt, tang, dt));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public List<ModelPhong> searchPhong(String keyword) {
        List<ModelPhong> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM PhongTro WHERE username = ? AND (maPhong LIKE ? OR tenPhong LIKE ?)", 
                new String[]{UserSession.currentUser, "%" + keyword + "%", "%" + keyword + "%"});
        if (cursor.moveToFirst()) {
            do {
                String ma = cursor.getString(cursor.getColumnIndexOrThrow("maPhong"));
                String ten = cursor.getString(cursor.getColumnIndexOrThrow("tenPhong"));
                int gia = cursor.getInt(cursor.getColumnIndexOrThrow("giaThue"));
                String tt = cursor.getString(cursor.getColumnIndexOrThrow("trangThai"));
                String tang = cursor.getString(cursor.getColumnIndexOrThrow("tang"));
                String dt = cursor.getString(cursor.getColumnIndexOrThrow("dienTich"));
                list.add(new ModelPhong(ma, ten, String.valueOf(gia), tt, tang, dt));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public int getGiaThuePhong(String maPhong) {
        int giaThue = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT giaThue FROM PhongTro WHERE maPhong = ? AND username = ?", new String[]{maPhong, UserSession.currentUser});
        if (cursor.moveToFirst()) {
            giaThue = cursor.getInt(cursor.getColumnIndexOrThrow("giaThue"));
        }
        cursor.close();
        db.close();
        return giaThue;
    }

    // --- PHÂN HỆ 3: QUẢN LÝ NGƯỜI THUÊ ---
    public boolean checkCccdExist(String cccd) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM NguoiThue WHERE cccd = ? AND username = ?", new String[]{cccd, UserSession.currentUser});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    public boolean themNguoiThue(String hoTen, String sdt, String cccd, String phongThue, String queQuan, String ngaySinh, String ngayVao, String ngayDi, String soLuongNguoi) {
        if (checkCccdExist(cccd)) {
            return false;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("hoTen", hoTen);
        values.put("soDienThoai", sdt);
        values.put("cccd", cccd);
        values.put("phongThue", phongThue);
        values.put("queQuan", queQuan);
        values.put("ngaySinh", ngaySinh);
        values.put("ngayChuyenVao", ngayVao);
        values.put("ngayChuyenDi", ngayDi);
        values.put("soLuongNguoi", soLuongNguoi);
        values.put("username", UserSession.currentUser);
        long result = db.insert("NguoiThue", null, values);
        db.close();
        return result != -1;
    }

    public boolean suaNguoiThue(String oldCccd, String hoTen, String sdt, String newCccd, String phongThue, String queQuan, String ngaySinh, String ngayVao, String ngayDi, String soLuongNguoi) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("hoTen", hoTen);
        values.put("soDienThoai", sdt);
        values.put("cccd", newCccd);
        values.put("phongThue", phongThue);
        values.put("queQuan", queQuan);
        values.put("ngaySinh", ngaySinh);
        values.put("ngayChuyenVao", ngayVao);
        values.put("ngayChuyenDi", ngayDi);
        values.put("soLuongNguoi", soLuongNguoi);
        int rows = db.update("NguoiThue", values, "cccd = ? AND username = ?", new String[]{oldCccd, UserSession.currentUser});
        db.close();
        return rows > 0;
    }

    public boolean xoaNguoiThue(String cccd) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete("NguoiThue", "cccd = ? AND username = ?", new String[]{cccd, UserSession.currentUser});
        db.close();
        return rows > 0;
    }

    public List<ModelNguoiThue> getAllNguoiThue() {
        List<ModelNguoiThue> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM NguoiThue WHERE username = ?", new String[]{UserSession.currentUser});
        if (cursor.moveToFirst()) {
            do {
                String hoTen = cursor.getString(cursor.getColumnIndexOrThrow("hoTen"));
                String sdt = cursor.getString(cursor.getColumnIndexOrThrow("soDienThoai"));
                String cccd = cursor.getString(cursor.getColumnIndexOrThrow("cccd"));
                String phongThue = cursor.getString(cursor.getColumnIndexOrThrow("phongThue"));
                String queQuan = cursor.getString(cursor.getColumnIndexOrThrow("queQuan"));
                String ngaySinh = cursor.getString(cursor.getColumnIndexOrThrow("ngaySinh"));
                String ngayVao = cursor.getString(cursor.getColumnIndexOrThrow("ngayChuyenVao"));
                String ngayDi = cursor.getString(cursor.getColumnIndexOrThrow("ngayChuyenDi"));
                String soLuongNguoi = cursor.getString(cursor.getColumnIndexOrThrow("soLuongNguoi"));
                list.add(new ModelNguoiThue(hoTen, sdt, cccd, phongThue, queQuan, ngaySinh, ngayVao, ngayDi, soLuongNguoi));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    // --- PHÂN HỆ 5: QUẢN LÝ ĐIỆN NƯỚC (NÂNG CẤP LẤY CHỈ SỐ CŨ CỦA THÁNG TRƯỚC) ---
    public double[] getChisoDienNuocMoiNhatByMaPhong(String maPhong) {
        double[] chiso = new double[]{0, 0}; // [dienMoi, nuocMoi]
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT dienMoi, nuocMoi FROM DienNuoc WHERE maPhong = ? AND username = ? ORDER BY id DESC LIMIT 1", new String[]{maPhong, UserSession.currentUser});
        if (cursor.moveToFirst()) {
            chiso[0] = cursor.getDouble(0);
            chiso[1] = cursor.getDouble(1);
        }
        cursor.close();
        db.close();
        return chiso;
    }

    public boolean themDienNuoc(String maPhong, String thangGhi, double dienCu, double dienMoi, double nuocCu, double nuocMoi, double tienDichVu, double tongTien) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("maPhong", maPhong);
        values.put("thangGhi", thangGhi);
        values.put("dienCu", dienCu);
        values.put("dienMoi", dienMoi);
        values.put("nuocCu", nuocCu);
        values.put("nuocMoi", nuocMoi);
        values.put("tienDichVu", tienDichVu);
        values.put("tongTien", tongTien);
        values.put("username", UserSession.currentUser);
        long result = db.insert("DienNuoc", null, values);
        db.close();
        return result != -1;
    }

    public List<ModelDienNuoc> getAllDienNuoc() {
        List<ModelDienNuoc> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM DienNuoc WHERE username = ? ORDER BY id DESC", new String[]{UserSession.currentUser});
        if (cursor.moveToFirst()) {
            do {
                String maPhong = cursor.getString(cursor.getColumnIndexOrThrow("maPhong"));
                String thangGhi = cursor.getString(cursor.getColumnIndexOrThrow("thangGhi"));
                double dienCu = cursor.getDouble(cursor.getColumnIndexOrThrow("dienCu"));
                double dienMoi = cursor.getDouble(cursor.getColumnIndexOrThrow("dienMoi"));
                double nuocCu = cursor.getDouble(cursor.getColumnIndexOrThrow("nuocCu"));
                double nuocMoi = cursor.getDouble(cursor.getColumnIndexOrThrow("nuocMoi"));
                double tienDichVu = cursor.getDouble(cursor.getColumnIndexOrThrow("tienDichVu"));
                double tongTien = cursor.getDouble(cursor.getColumnIndexOrThrow("tongTien"));
                list.add(new ModelDienNuoc(maPhong, thangGhi, dienCu, dienMoi, nuocCu, nuocMoi, tienDichVu, tongTien));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public double getTongTienDienNuoc(String maPhong) {
        double tongTien = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT tongTien FROM DienNuoc WHERE maPhong = ? AND username = ? ORDER BY id DESC LIMIT 1", new String[]{maPhong, UserSession.currentUser});
        if (cursor.moveToFirst()) {
            tongTien = cursor.getDouble(cursor.getColumnIndexOrThrow("tongTien"));
        }
        cursor.close();
        db.close();
        return tongTien;
    }

    // --- PHÂN HỆ 6 & 7: QUẢN LÝ HÓA ĐƠN & THANH TOÁN ---
    public boolean themHoaDon(String maHoaDon, String maPhong, double tongTien) {
        return themHoaDon(maHoaDon, maPhong, tongTien, "Chưa thanh toán");
    }

    public boolean themHoaDon(String maHoaDon, String maPhong, double tongTien, String trangThai) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("maHoaDon", maHoaDon);
        values.put("maPhong", maPhong);
        values.put("tongTien", tongTien);
        values.put("trangThai", trangThai);
        values.put("congNo", trangThai.contains("Chưa") ? tongTien : 0);
        values.put("username", UserSession.currentUser);
        long result = db.insert("HoaDon", null, values);
        db.close();
        return result != -1;
    }

    public boolean updateTrangThaiHoaDon(String maHoaDon, String trangThaiMoi) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("trangThai", trangThaiMoi);
        if (trangThaiMoi.contains("Đã thanh toán")) {
            values.put("congNo", 0);
        }
        int rows = db.update("HoaDon", values, "maHoaDon = ? AND username = ?", new String[]{maHoaDon, UserSession.currentUser});
        db.close();
        return rows > 0;
    }

    public List<String> getAllHoaDonString() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM HoaDon WHERE username = ?", new String[]{UserSession.currentUser});
        if (cursor.moveToFirst()) {
            do {
                String maHD = cursor.getString(cursor.getColumnIndexOrThrow("maHoaDon"));
                String maP = cursor.getString(cursor.getColumnIndexOrThrow("maPhong"));
                double tong = cursor.getDouble(cursor.getColumnIndexOrThrow("tongTien"));
                String tt = cursor.getString(cursor.getColumnIndexOrThrow("trangThai"));
                list.add("Mã HD: " + maHD + " | Phòng: " + maP + " | Tổng: " + tong + " VNĐ\nTrạng thái: " + (tt != null ? tt : "Chưa thanh toán"));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public double getTongCongNo() {
        double tongCongNo = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(congNo) FROM HoaDon WHERE username = ? AND trangThai != 'Đã hủy'", new String[]{UserSession.currentUser});
        if (cursor.moveToFirst() && !cursor.isNull(0)) {
            tongCongNo = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return tongCongNo;
    }

    // --- PHÂN HỆ 4: QUẢN LÝ HỢP ĐỒNG ---
    public boolean themHopDongChiTiet(String maHopDong, String maPhong, String nguoiDaiDien, String nguoiOCung,
                                       String ngayBatDau, String ngayKetThuc, double giaThue, double tienCoc,
                                       String ngayThanhToanHangThang, String ngayThanhToanDauTien, String ghiChu) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("maHopDong", maHopDong);
        values.put("maPhong", maPhong);
        values.put("nguoiDaiDien", nguoiDaiDien);
        values.put("nguoiOCung", nguoiOCung);
        values.put("ngayBatDau", ngayBatDau);
        values.put("ngayKetThuc", ngayKetThuc);
        values.put("giaThue", giaThue);
        values.put("tienCoc", tienCoc);
        values.put("ngayThanhToanHangThang", ngayThanhToanHangThang);
        values.put("ngayThanhToanDauTien", ngayThanhToanDauTien);
        values.put("ghiChu", ghiChu);
        values.put("trangThai", "Đang hiệu lực");
        values.put("username", UserSession.currentUser);
        long result = db.insert("HopDong", null, values);
        db.close();
        return result != -1;
    }

    public List<ModelHopDong> getAllHopDong() {
        List<ModelHopDong> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM HopDong WHERE username = ?", new String[]{UserSession.currentUser});
        if (cursor.moveToFirst()) {
            do {
                String maHD = cursor.getString(cursor.getColumnIndexOrThrow("maHopDong"));
                String maP = cursor.getString(cursor.getColumnIndexOrThrow("maPhong"));
                String nguoiDD = cursor.getString(cursor.getColumnIndexOrThrow("nguoiDaiDien"));
                String nguoiOC = cursor.getString(cursor.getColumnIndexOrThrow("nguoiOCung"));
                String ngayBD = cursor.getString(cursor.getColumnIndexOrThrow("ngayBatDau"));
                String ngayKT = cursor.getString(cursor.getColumnIndexOrThrow("ngayKetThuc"));
                double gia = cursor.getDouble(cursor.getColumnIndexOrThrow("giaThue"));
                double coc = cursor.getDouble(cursor.getColumnIndexOrThrow("tienCoc"));
                String ngayTTThang = cursor.getString(cursor.getColumnIndexOrThrow("ngayThanhToanHangThang"));
                String ngayTTDau = cursor.getString(cursor.getColumnIndexOrThrow("ngayThanhToanDauTien"));
                String ghiChu = cursor.getString(cursor.getColumnIndexOrThrow("ghiChu"));
                String trangThai = cursor.getString(cursor.getColumnIndexOrThrow("trangThai"));

                list.add(new ModelHopDong(maHD, maP, nguoiDD, nguoiOC, ngayBD, ngayKT, gia, coc, ngayTTThang, ngayTTDau, ghiChu, trangThai));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public List<String> getAllHopDongString() {
        List<String> list = new ArrayList<>();
        List<ModelHopDong> hopDongList = getAllHopDong();
        for (ModelHopDong hd : hopDongList) {
            list.add("HĐ: " + hd.getMaHopDong() + " | Phòng: " + hd.getMaPhong() + " | ĐD: " + hd.getNguoiDaiDien() + " (" + hd.getTrangThai() + ")");
        }
        return list;
    }

    // --- SỔ THU CHI & BẢO TRÌ ---
    public boolean themThuChi(String loai, double soTien, String danhMuc, String ghiChu, String ngayGhi) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("loai", loai);
        values.put("soTien", soTien);
        values.put("danhMuc", danhMuc);
        values.put("ghiChu", ghiChu);
        values.put("ngayGhi", ngayGhi);
        values.put("username", UserSession.currentUser);
        long result = db.insert("ThuChi", null, values);
        db.close();
        return result != -1;
    }

    public List<String> getAllThuChiString() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM ThuChi WHERE username = ? ORDER BY id DESC", new String[]{UserSession.currentUser});
        if (cursor.moveToFirst()) {
            do {
                String loai = cursor.getString(cursor.getColumnIndexOrThrow("loai"));
                double soTien = cursor.getDouble(cursor.getColumnIndexOrThrow("soTien"));
                String danhMuc = cursor.getString(cursor.getColumnIndexOrThrow("danhMuc"));
                String ngayGhi = cursor.getString(cursor.getColumnIndexOrThrow("ngayGhi"));
                String dau = loai.equals("CHI") ? "-" : "+";
                list.add("[" + ngayGhi + "] " + danhMuc + ": " + dau + String.format(java.util.Locale.getDefault(), "%,.0f", soTien) + " VNĐ");
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public double getTongChiPhi() {
        double tongChi = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(soTien) FROM ThuChi WHERE username = ? AND loai = 'CHI'", new String[]{UserSession.currentUser});
        if (cursor.moveToFirst() && !cursor.isNull(0)) {
            tongChi = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return tongChi;
    }
}
