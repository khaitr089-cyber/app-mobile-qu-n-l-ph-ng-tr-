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

public class QuanLyCoSoDuLieu extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "quanlyphongtro.db";
<<<<<<< Updated upstream
    private static final int DATABASE_VERSION = 12; // Tăng version để thêm trường số lượng người ở
=======
    private static final int DATABASE_VERSION = 12;
>>>>>>> Stashed changes

    public QuanLyCoSoDuLieu(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Tạo bảng Người Dùng
        String sqlUser = "CREATE TABLE NguoiDung (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "tenDangNhap TEXT UNIQUE, " +
                "matKhau TEXT)";
        db.execSQL(sqlUser);
        db.execSQL("INSERT INTO NguoiDung(tenDangNhap, matKhau) VALUES('admin', '123456')");

        // 2. Tạo bảng Phòng Trọ
        String sqlPhong = "CREATE TABLE PhongTro (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "maPhong TEXT, " +
                "tenPhong TEXT, " +
                "giaThue INTEGER, " +
                "trangThai TEXT, " +
                "tang TEXT, " +
                "dienTich TEXT, " +
                "username TEXT)";
        db.execSQL(sqlPhong);

<<<<<<< Updated upstream
        // 3. Tạo bảng Người Thuê (Bổ sung ngaySinh, ngayChuyenVao, ngayChuyenDi, soLuongNguoi)
=======
        // 3. Tạo bảng Người Thuê
>>>>>>> Stashed changes
        String sqlNguoiThue = "CREATE TABLE NguoiThue (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "hoTen TEXT, " +
                "soDienThoai TEXT, " +
                "cccd TEXT, " +
                "phongThue TEXT, " +
                "queQuan TEXT, " +
                "ngaySinh TEXT, " +
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
                "trangThai TEXT, " +
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
                "trangThai TEXT)";
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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS NguoiDung");
        db.execSQL("DROP TABLE IF EXISTS PhongTro");
        db.execSQL("DROP TABLE IF EXISTS NguoiThue");
        db.execSQL("DROP TABLE IF EXISTS HoaDon");
        db.execSQL("DROP TABLE IF EXISTS HopDong");
        db.execSQL("DROP TABLE IF EXISTS DienNuoc");
        onCreate(db);
    }

    // --- CÁC HÀM XỬ LÝ NGƯỜI DÙNG ---
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

    public boolean dangky(String taiKhoan, String matKhau) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tenDangNhap", taiKhoan);
        values.put("matKhau", matKhau);
        long result = db.insert("NguoiDung", null, values);
        db.close();
        return result != -1;
    }

    // --- CÁC HÀM XỬ LÝ PHÒNG TRỌ ---
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

    // --- CÁC HÀM XỬ LÝ NGƯỜI THUÊ ---
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

    // --- CÁC HÀM XỬ LÝ HÓA ĐƠN ---
    public boolean themHoaDon(String maHoaDon, String maPhong, double tongTien, String trangThai) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("maHoaDon", maHoaDon);
        values.put("maPhong", maPhong);
        values.put("tongTien", tongTien);
        values.put("trangThai", trangThai);
        values.put("username", UserSession.currentUser);
        long result = db.insert("HoaDon", null, values);
        db.close();
        return result != -1;
    }

    public boolean updateTrangThaiHoaDon(String maHoaDon, String trangThaiMoi) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("trangThai", trangThaiMoi);
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

    // --- CÁC HÀM XỬ LÝ HỢP ĐỒNG ---
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
        long result = db.insert("HopDong", null, values);
        db.close();
        return result != -1;
    }

    public List<ModelHopDong> getAllHopDong() {
        List<ModelHopDong> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM HopDong", null);
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

    // --- CÁC HÀM XỬ LÝ ĐIỆN NƯỚC DỊCH VỤ ---
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
}
