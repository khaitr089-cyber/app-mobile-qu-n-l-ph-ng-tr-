package com.example.quanlyphongtro;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.example.quanlyphongtro.model.ModelDienNuoc;
import com.example.quanlyphongtro.model.ModelHopDong;
import com.example.quanlyphongtro.model.ModelNguoiThue;
import com.example.quanlyphongtro.model.ModelPhong;

public class QuanLyCoSoDuLieu extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "quanlyphongtro.db";
    private static final int DATABASE_VERSION = 20; // Tăng version lên 20 để tránh lỗi downgrade

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

        // 3. Tạo bảng Người Thuê
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
                "maHoaDon TEXT, " +
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

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    private String getUser() {
        return (UserSession.currentUser != null && !UserSession.currentUser.trim().isEmpty()) ? UserSession.currentUser : "admin";
    }

    // --- CÁC HÀM XỬ LÝ NGƯỜI DÙNG ---
    public boolean dangnhap(String taiKhoan, String matKhau) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS NguoiDung (id INTEGER PRIMARY KEY AUTOINCREMENT, tenDangNhap TEXT UNIQUE, matKhau TEXT)");
            Cursor checkAdmin = db.rawQuery("SELECT * FROM NguoiDung WHERE tenDangNhap = 'admin'", null);
            if (!checkAdmin.moveToFirst()) {
                db.execSQL("INSERT OR IGNORE INTO NguoiDung(tenDangNhap, matKhau) VALUES('admin', '123456')");
            }
            checkAdmin.close();

            cursor = db.rawQuery(
                    "SELECT * FROM NguoiDung WHERE tenDangNhap=? AND matKhau=?",
                    new String[]{taiKhoan, matKhau}
            );
            boolean ketQua = cursor.moveToFirst();
            cursor.close();
            db.close();
            return ketQua;
        } catch (Exception e) {
            if (cursor != null) cursor.close();
            db.close();
            return false;
        }
    }

    public boolean dangkyFull(String taiKhoan, String matKhau, String hoTen, String sdt, String cccd) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS NguoiDung (id INTEGER PRIMARY KEY AUTOINCREMENT, tenDangNhap TEXT UNIQUE, matKhau TEXT)");
            ContentValues values = new ContentValues();
            values.put("tenDangNhap", taiKhoan);
            values.put("matKhau", matKhau);
            long result = db.insert("NguoiDung", null, values);
            db.close();
            return result != -1;
        } catch (Exception e) {
            db.close();
            return false;
        }
    }

    // --- CÁC HÀM XỬ LÝ PHÒNG TRỌ (CRUD) ---
    public boolean checkMaPhongExist(String maPhong) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM PhongTro WHERE maPhong = ? AND username = ?", new String[]{maPhong, getUser()});
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
            String cleanGia = giaThueStr != null ? giaThueStr.replaceAll("[^0-9]", "") : "";
            long giaThue = cleanGia.isEmpty() ? 0 : Long.parseLong(cleanGia);
            values.put("giaThue", giaThue);
        } catch (Exception e) {
            values.put("giaThue", 0);
        }
        values.put("trangThai", trangThai);
        values.put("tang", tang);
        values.put("dienTich", dienTich);
        values.put("username", getUser());
        long result = db.insert("PhongTro", null, values);
        db.close();
        return result != -1;
    }

    public boolean suaPhong(String maPhong, String tenPhong, String giaThueStr, String trangThai, String tang, String dienTich) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tenPhong", tenPhong);
        try {
            String cleanGia = giaThueStr != null ? giaThueStr.replaceAll("[^0-9]", "") : "";
            long giaThue = cleanGia.isEmpty() ? 0 : Long.parseLong(cleanGia);
            values.put("giaThue", giaThue);
        } catch (Exception e) {
            values.put("giaThue", 0);
        }
        values.put("trangThai", trangThai);
        values.put("tang", tang);
        values.put("dienTich", dienTich);
        int rows = db.update("PhongTro", values, "maPhong = ? AND username = ?", new String[]{maPhong, getUser()});
        db.close();
        return rows > 0;
    }

    public boolean updateTrangThaiPhong(String maPhong, String trangThaiMoi) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("trangThai", trangThaiMoi);
        int rows = db.update("PhongTro", values, "maPhong = ? AND username = ?", new String[]{maPhong, getUser()});
        db.close();
        return rows > 0;
    }

    public boolean xoaPhong(String maPhong) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete("PhongTro", "maPhong = ? AND username = ?", new String[]{maPhong, getUser()});
        db.close();
        return rows > 0;
    }

    public List<ModelPhong> getAllPhong() {
        List<ModelPhong> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM PhongTro WHERE username = ?", new String[]{getUser()});
        } catch (Exception e) {
            cursor = db.rawQuery("SELECT * FROM PhongTro", null);
        }
        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    String ma = cursor.getString(cursor.getColumnIndexOrThrow("maPhong"));
                    String ten = cursor.getString(cursor.getColumnIndexOrThrow("tenPhong"));
                    int gia = cursor.getInt(cursor.getColumnIndexOrThrow("giaThue"));
                    String tt = cursor.getString(cursor.getColumnIndexOrThrow("trangThai"));
                    String tang = cursor.getColumnIndex("tang") != -1 ? cursor.getString(cursor.getColumnIndexOrThrow("tang")) : "";
                    String dt = cursor.getColumnIndex("dienTich") != -1 ? cursor.getString(cursor.getColumnIndexOrThrow("dienTich")) : "";
                    list.add(new ModelPhong(ma, ten, String.valueOf(gia), tt, tang, dt));
                } catch (Exception ignored) {}
            } while (cursor.moveToNext());
        }
        if (cursor != null) cursor.close();
        db.close();
        return list;
    }

    public List<ModelPhong> searchPhong(String keyword) {
        List<ModelPhong> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM PhongTro WHERE username = ? AND (maPhong LIKE ? OR tenPhong LIKE ?)", 
                    new String[]{getUser(), "%" + keyword + "%", "%" + keyword + "%"});
        } catch (Exception e) {
            cursor = db.rawQuery("SELECT * FROM PhongTro WHERE maPhong LIKE ? OR tenPhong LIKE ?", 
                    new String[]{"%" + keyword + "%", "%" + keyword + "%"});
        }
        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    String ma = cursor.getString(cursor.getColumnIndexOrThrow("maPhong"));
                    String ten = cursor.getString(cursor.getColumnIndexOrThrow("tenPhong"));
                    int gia = cursor.getInt(cursor.getColumnIndexOrThrow("giaThue"));
                    String tt = cursor.getString(cursor.getColumnIndexOrThrow("trangThai"));
                    String tang = cursor.getColumnIndex("tang") != -1 ? cursor.getString(cursor.getColumnIndexOrThrow("tang")) : "";
                    String dt = cursor.getColumnIndex("dienTich") != -1 ? cursor.getString(cursor.getColumnIndexOrThrow("dienTich")) : "";
                    list.add(new ModelPhong(ma, ten, String.valueOf(gia), tt, tang, dt));
                } catch (Exception ignored) {}
            } while (cursor.moveToNext());
        }
        if (cursor != null) cursor.close();
        db.close();
        return list;
    }

    public List<ModelPhong> getPhongByTrangThai(String trangThai) {
        List<ModelPhong> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM PhongTro WHERE username = ? AND trangThai LIKE ?", new String[]{getUser(), "%" + trangThai + "%"});
        } catch (Exception e) {
            cursor = db.rawQuery("SELECT * FROM PhongTro WHERE trangThai LIKE ?", new String[]{"%" + trangThai + "%"});
        }
        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    String ma = cursor.getString(cursor.getColumnIndexOrThrow("maPhong"));
                    String ten = cursor.getString(cursor.getColumnIndexOrThrow("tenPhong"));
                    int gia = cursor.getInt(cursor.getColumnIndexOrThrow("giaThue"));
                    String tt = cursor.getString(cursor.getColumnIndexOrThrow("trangThai"));
                    String tang = cursor.getColumnIndex("tang") != -1 ? cursor.getString(cursor.getColumnIndexOrThrow("tang")) : "";
                    String dt = cursor.getColumnIndex("dienTich") != -1 ? cursor.getString(cursor.getColumnIndexOrThrow("dienTich")) : "";
                    list.add(new ModelPhong(ma, ten, String.valueOf(gia), tt, tang, dt));
                } catch (Exception ignored) {}
            } while (cursor.moveToNext());
        }
        if (cursor != null) cursor.close();
        db.close();
        return list;
    }

    public int getGiaThuePhong(String maPhong) {
        int giaThue = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT giaThue FROM PhongTro WHERE maPhong = ? AND username = ?", new String[]{maPhong, getUser()});
        } catch (Exception e) {
            cursor = db.rawQuery("SELECT giaThue FROM PhongTro WHERE maPhong = ?", new String[]{maPhong});
        }
        if (cursor != null && cursor.moveToFirst()) {
            giaThue = cursor.getInt(cursor.getColumnIndexOrThrow("giaThue"));
        }
        if (cursor != null) cursor.close();
        db.close();
        return giaThue;
    }

    // --- CÁC HÀM XỬ LÝ NGƯỜI THUÊ (CRUD) ---
    public boolean checkCccdExist(String cccd) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM NguoiThue WHERE cccd = ? AND username = ?", new String[]{cccd, getUser()});
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
        values.put("username", getUser());
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
        int rows = db.update("NguoiThue", values, "cccd = ? AND username = ?", new String[]{oldCccd, getUser()});
        db.close();
        return rows > 0;
    }

    public boolean xoaNguoiThue(String cccd) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete("NguoiThue", "cccd = ? AND username = ?", new String[]{cccd, getUser()});
        db.close();
        return rows > 0;
    }

    public List<ModelNguoiThue> getAllNguoiThue() {
        List<ModelNguoiThue> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM NguoiThue WHERE username = ?", new String[]{getUser()});
        } catch (Exception e) {
            cursor = db.rawQuery("SELECT * FROM NguoiThue", null);
        }
        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    String hoTen = cursor.getString(cursor.getColumnIndexOrThrow("hoTen"));
                    String sdt = cursor.getString(cursor.getColumnIndexOrThrow("soDienThoai"));
                    String cccd = cursor.getString(cursor.getColumnIndexOrThrow("cccd"));
                    String phongThue = cursor.getString(cursor.getColumnIndexOrThrow("phongThue"));
                    String queQuan = cursor.getString(cursor.getColumnIndexOrThrow("queQuan"));
                    String ngaySinh = cursor.getColumnIndex("ngaySinh") != -1 ? cursor.getString(cursor.getColumnIndexOrThrow("ngaySinh")) : "";
                    String ngayVao = cursor.getColumnIndex("ngayChuyenVao") != -1 ? cursor.getString(cursor.getColumnIndexOrThrow("ngayChuyenVao")) : "";
                    String ngayDi = cursor.getColumnIndex("ngayChuyenDi") != -1 ? cursor.getString(cursor.getColumnIndexOrThrow("ngayChuyenDi")) : "";
                    String soLuongNguoi = cursor.getColumnIndex("soLuongNguoi") != -1 ? cursor.getString(cursor.getColumnIndexOrThrow("soLuongNguoi")) : "1";
                    list.add(new ModelNguoiThue(hoTen, sdt, cccd, phongThue, queQuan, ngaySinh, ngayVao, ngayDi, soLuongNguoi));
                } catch (Exception ignored) {}
            } while (cursor.moveToNext());
        }
        if (cursor != null) cursor.close();
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
        values.put("username", getUser());
        long result = db.insert("HoaDon", null, values);
        db.close();
        return result != -1;
    }

    public List<String> getAllHoaDonString() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM HoaDon WHERE username = ?", new String[]{getUser()});
        } catch (Exception e) {
            cursor = db.rawQuery("SELECT * FROM HoaDon", null);
        }
        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    String maHD = cursor.getString(cursor.getColumnIndexOrThrow("maHoaDon"));
                    String maP = cursor.getString(cursor.getColumnIndexOrThrow("maPhong"));
                    double tong = cursor.getDouble(cursor.getColumnIndexOrThrow("tongTien"));
                    String trangThai = cursor.getColumnIndex("trangThai") != -1 ? cursor.getString(cursor.getColumnIndexOrThrow("trangThai")) : "Chưa thanh toán";
                    list.add("Mã HD: " + maHD + " | Phòng: " + maP + " | Tổng: " + tong + " VNĐ (" + trangThai + ")");
                } catch (Exception ignored) {}
            } while (cursor.moveToNext());
        }
        if (cursor != null) cursor.close();
        db.close();
        return list;
    }

    public boolean updateTrangThaiHoaDon(String maHoaDon, String trangThaiMoi) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("trangThai", trangThaiMoi);
        int rows = db.update("HoaDon", values, "maHoaDon = ? AND username = ?", new String[]{maHoaDon, getUser()});
        db.close();
        return rows > 0;
    }

    // --- CÁC HÀM XỬ LÝ ĐIỆN NƯỚC ---
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
        values.put("username", getUser());
        long result = db.insert("DienNuoc", null, values);
        db.close();
        return result != -1;
    }

    public List<ModelDienNuoc> getAllDienNuoc() {
        List<ModelDienNuoc> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM DienNuoc WHERE username = ?", new String[]{getUser()});
        } catch (Exception e) {
            cursor = db.rawQuery("SELECT * FROM DienNuoc", null);
        }
        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    String maPhong = cursor.getString(cursor.getColumnIndexOrThrow("maPhong"));
                    String thangGhi = cursor.getString(cursor.getColumnIndexOrThrow("thangGhi"));
                    double dienCu = cursor.getDouble(cursor.getColumnIndexOrThrow("dienCu"));
                    double dienMoi = cursor.getDouble(cursor.getColumnIndexOrThrow("dienMoi"));
                    double nuocCu = cursor.getDouble(cursor.getColumnIndexOrThrow("nuocCu"));
                    double nuocMoi = cursor.getDouble(cursor.getColumnIndexOrThrow("nuocMoi"));
                    double tienDichVu = cursor.getDouble(cursor.getColumnIndexOrThrow("tienDichVu"));
                    double tongTien = cursor.getDouble(cursor.getColumnIndexOrThrow("tongTien"));
                    list.add(new ModelDienNuoc(maPhong, thangGhi, dienCu, dienMoi, nuocCu, nuocMoi, tienDichVu, tongTien));
                } catch (Exception ignored) {}
            } while (cursor.moveToNext());
        }
        if (cursor != null) cursor.close();
        db.close();
        return list;
    }

    public double[] getChisoDienNuocMoiNhatByMaPhong(String maPhong) {
        double[] ketQua = new double[]{0, 0}; // [dienMoi, nuocMoi]
        SQLiteDatabase db = this.getReadableDatabase();
        String maClean = maPhong.contains(" ") ? maPhong.split(" ")[0].trim() : maPhong.trim();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT dienMoi, nuocMoi FROM DienNuoc WHERE (maPhong = ? OR maPhong LIKE ?) AND username = ? ORDER BY id DESC LIMIT 1", new String[]{maClean, maClean + " %", getUser()});
        } catch (Exception e) {
            cursor = db.rawQuery("SELECT dienMoi, nuocMoi FROM DienNuoc WHERE (maPhong = ? OR maPhong LIKE ?) ORDER BY id DESC LIMIT 1", new String[]{maClean, maClean + " %"});
        }
        if (cursor != null && cursor.moveToFirst()) {
            ketQua[0] = cursor.getDouble(cursor.getColumnIndexOrThrow("dienMoi"));
            ketQua[1] = cursor.getDouble(cursor.getColumnIndexOrThrow("nuocMoi"));
        }
        if (cursor != null) cursor.close();
        db.close();
        return ketQua;
    }

    public double getTongTienDienNuoc(String maPhong) {
        double tongTien = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String maClean = maPhong.contains(" ") ? maPhong.split(" ")[0].trim() : maPhong.trim();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT tongTien FROM DienNuoc WHERE (maPhong = ? OR maPhong LIKE ?) AND username = ? ORDER BY id DESC LIMIT 1", new String[]{maClean, maClean + " %", getUser()});
        } catch (Exception e) {
            cursor = db.rawQuery("SELECT tongTien FROM DienNuoc WHERE (maPhong = ? OR maPhong LIKE ?) ORDER BY id DESC LIMIT 1", new String[]{maClean, maClean + " %"});
        }
        if (cursor != null && cursor.moveToFirst()) {
            tongTien = cursor.getDouble(cursor.getColumnIndexOrThrow("tongTien"));
        }
        if (cursor != null) cursor.close();
        db.close();
        return tongTien;
    }

    // --- CÁC HÀM XỬ LÝ THU CHI ---
    public boolean themThuChi(String loai, double soTien, String danhMuc, String ghiChu, String ngayGhi) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("loai", loai);
        values.put("soTien", soTien);
        values.put("danhMuc", danhMuc);
        values.put("ghiChu", ghiChu);
        values.put("ngayGhi", ngayGhi);
        values.put("username", getUser());
        long result = db.insert("ThuChi", null, values);
        db.close();
        return result != -1;
    }

    public double getTongChiPhi() {
        double tongChi = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT SUM(soTien) FROM ThuChi WHERE username = ? AND loai = 'CHI'", new String[]{getUser()});
        } catch (Exception e) {
            cursor = db.rawQuery("SELECT SUM(soTien) FROM ThuChi WHERE loai = 'CHI'", null);
        }
        if (cursor != null && cursor.moveToFirst()) {
            tongChi = cursor.getDouble(0);
        }
        if (cursor != null) cursor.close();
        db.close();
        return tongChi;
    }

    public List<String> getAllThuChiString() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM ThuChi WHERE username = ? ORDER BY id DESC", new String[]{getUser()});
        } catch (Exception e) {
            cursor = db.rawQuery("SELECT * FROM ThuChi ORDER BY id DESC", null);
        }
        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    String loai = cursor.getString(cursor.getColumnIndexOrThrow("loai"));
                    double soTien = cursor.getDouble(cursor.getColumnIndexOrThrow("soTien"));
                    String danhMuc = cursor.getString(cursor.getColumnIndexOrThrow("danhMuc"));
                    String ngayGhi = cursor.getString(cursor.getColumnIndexOrThrow("ngayGhi"));
                    list.add("[" + loai + "] " + danhMuc + ": " + String.format(Locale.getDefault(), "%,.0f VNĐ", soTien) + " (" + ngayGhi + ")");
                } catch (Exception ignored) {}
            } while (cursor.moveToNext());
        }
        if (cursor != null) cursor.close();
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
        values.put("username", getUser());
        long result = db.insert("HopDong", null, values);
        db.close();
        return result != -1;
    }

    public List<ModelHopDong> getAllHopDong() {
        List<ModelHopDong> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM HopDong WHERE username = ?", new String[]{getUser()});
        } catch (Exception e) {
            try {
                cursor = db.rawQuery("SELECT * FROM HopDong", null);
            } catch (Exception ex) {
                db.close();
                return list;
            }
        }
        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    String maHD = cursor.getString(cursor.getColumnIndexOrThrow("maHopDong"));
                    String maP = cursor.getString(cursor.getColumnIndexOrThrow("maPhong"));
                    String nguoiDD = cursor.getString(cursor.getColumnIndexOrThrow("nguoiDaiDien"));
                    String nguoiOC = cursor.getColumnIndex("nguoiOCung") != -1 ? cursor.getString(cursor.getColumnIndexOrThrow("nguoiOCung")) : "";
                    String ngayBD = cursor.getString(cursor.getColumnIndexOrThrow("ngayBatDau"));
                    String ngayKT = cursor.getString(cursor.getColumnIndexOrThrow("ngayKetThuc"));
                    double gia = cursor.getDouble(cursor.getColumnIndexOrThrow("giaThue"));
                    double coc = cursor.getDouble(cursor.getColumnIndexOrThrow("tienCoc"));
                    String ngayTTThang = cursor.getColumnIndex("ngayThanhToanHangThang") != -1 ? cursor.getString(cursor.getColumnIndexOrThrow("ngayThanhToanHangThang")) : "";
                    String ngayTTDau = cursor.getColumnIndex("ngayThanhToanDauTien") != -1 ? cursor.getString(cursor.getColumnIndexOrThrow("ngayThanhToanDauTien")) : "";
                    String ghiChu = cursor.getColumnIndex("ghiChu") != -1 ? cursor.getString(cursor.getColumnIndexOrThrow("ghiChu")) : "";
                    String trangThai = cursor.getColumnIndex("trangThai") != -1 ? cursor.getString(cursor.getColumnIndexOrThrow("trangThai")) : "Đang hiệu lực";

                    list.add(new ModelHopDong(maHD, maP, nguoiDD, nguoiOC, ngayBD, ngayKT, gia, coc, ngayTTThang, ngayTTDau, ghiChu, trangThai));
                } catch (Exception ignored) {}
            } while (cursor.moveToNext());
        }
        if (cursor != null) cursor.close();
        db.close();
        return list;
    }

    public List<String> getAllHopDongString() {
        List<String> list = new ArrayList<>();
        List<ModelHopDong> hopDongList = getAllHopDong();
        if (hopDongList != null) {
            for (ModelHopDong hd : hopDongList) {
                if (hd != null) {
                    list.add("HĐ: " + hd.getMaHopDong() + " | Phòng: " + hd.getMaPhong() + " | ĐD: " + hd.getNguoiDaiDien() + " (" + hd.getTrangThai() + ")");
                }
            }
        }
        return list;
    }
}
