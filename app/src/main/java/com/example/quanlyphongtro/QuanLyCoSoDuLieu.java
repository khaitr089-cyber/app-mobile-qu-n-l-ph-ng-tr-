package com.example.quanlyphongtro;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

import com.example.quanlyphongtro.model.ModelNguoiThue;
import com.example.quanlyphongtro.model.ModelPhong;

public class QuanLyCoSoDuLieu extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "quanlyphongtro.db";
    private static final int DATABASE_VERSION = 5;

    public QuanLyCoSoDuLieu(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Tạo bảng Người Dùng phục vụ Đăng nhập & Đăng ký
        String sqlUser = "CREATE TABLE NguoiDung (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "tenDangNhap TEXT UNIQUE, " +
                "matKhau TEXT)";
        db.execSQL(sqlUser);
        db.execSQL("INSERT INTO NguoiDung(tenDangNhap, matKhau) VALUES('admin', '123456')");

        // 2. Tạo bảng Phòng Trọ
        String sqlPhong = "CREATE TABLE PhongTro (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "maPhong TEXT UNIQUE, " +
                "tenPhong TEXT, " +
                "giaThue INTEGER, " +
                "trangThai TEXT)";
        db.execSQL(sqlPhong);

        // 3. Tạo bảng Người Thuê
        String sqlNguoiThue = "CREATE TABLE NguoiThue (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "hoTen TEXT, " +
                "soDienThoai TEXT, " +
                "cccd TEXT UNIQUE, " +
                "phongThue TEXT, " +
                "queQuan TEXT)";
        db.execSQL(sqlNguoiThue);

        // 4. Tạo bảng Hóa Đơn
        String sqlHoaDon = "CREATE TABLE HoaDon (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "maHoaDon TEXT UNIQUE, " +
                "maPhong TEXT, " +
                "tongTien REAL)";
        db.execSQL(sqlHoaDon);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS NguoiDung");
        db.execSQL("DROP TABLE IF EXISTS PhongTro");
        db.execSQL("DROP TABLE IF EXISTS NguoiThue");
        db.execSQL("DROP TABLE IF EXISTS HoaDon");
        onCreate(db);
    }
//------------------------------------------------------------------------------------------------------
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

    // --- CÁC HÀM XỬ LÝ PHÒNG TRỌ (CRUD) ---
    public boolean themPhong(String maPhong, String tenPhong, String giaThueStr, String trangThai) {
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
        long result = db.insert("PhongTro", null, values);
        db.close();
        return result != -1;
    }

    public boolean suaPhong(String maPhong, String tenPhong, String giaThueStr, String trangThai) {
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
        int rows = db.update("PhongTro", values, "maPhong = ?", new String[]{maPhong});
        db.close();
        return rows > 0;
    }

    public boolean updateTrangThaiPhong(String maPhong, String trangThaiMoi) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("trangThai", trangThaiMoi);
        int rows = db.update("PhongTro", values, "maPhong = ?", new String[]{maPhong});
        db.close();
        return rows > 0;
    }

    public boolean xoaPhong(String maPhong) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete("PhongTro", "maPhong = ?", new String[]{maPhong});
        db.close();
        return rows > 0;
    }

    public List<ModelPhong> getAllPhong() {
        List<ModelPhong> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM PhongTro", null);
        if (cursor.moveToFirst()) {
            do {
                String ma = cursor.getString(cursor.getColumnIndexOrThrow("maPhong"));
                String ten = cursor.getString(cursor.getColumnIndexOrThrow("tenPhong"));
                int gia = cursor.getInt(cursor.getColumnIndexOrThrow("giaThue"));
                String tt = cursor.getString(cursor.getColumnIndexOrThrow("trangThai"));
                list.add(new ModelPhong(ma, ten, String.valueOf(gia), tt));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    // --- CÁC HÀM XỬ LÝ NGƯỜI THUÊ ---
    public boolean themNguoiThue(String hoTen, String sdt, String cccd, String phongThue, String queQuan) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("hoTen", hoTen);
        values.put("soDienThoai", sdt);
        values.put("cccd", cccd);
        values.put("phongThue", phongThue);
        values.put("queQuan", queQuan);
        long result = db.insert("NguoiThue", null, values);
        db.close();
        return result != -1;
    }

    public boolean suaNguoiThue(String hoTen, String sdt, String cccd, String phongThue, String queQuan) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("hoTen", hoTen);
        values.put("soDienThoai", sdt);
        values.put("phongThue", phongThue);
        values.put("queQuan", queQuan);
        int rows = db.update("NguoiThue", values, "cccd = ?", new String[]{cccd});
        db.close();
        return rows > 0;
    }

    public boolean xoaNguoiThue(String cccd) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete("NguoiThue", "cccd = ?", new String[]{cccd});
        db.close();
        return rows > 0;
    }

    public List<ModelNguoiThue> getAllNguoiThue() {
        List<ModelNguoiThue> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM NguoiThue", null);
        if (cursor.moveToFirst()) {
            do {
                String hoTen = cursor.getString(cursor.getColumnIndexOrThrow("hoTen"));
                String sdt = cursor.getString(cursor.getColumnIndexOrThrow("soDienThoai"));
                String cccd = cursor.getString(cursor.getColumnIndexOrThrow("cccd"));
                String phongThue = cursor.getString(cursor.getColumnIndexOrThrow("phongThue"));
                String queQuan = cursor.getString(cursor.getColumnIndexOrThrow("queQuan"));
                list.add(new ModelNguoiThue(hoTen, sdt, cccd, phongThue, queQuan));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    // --- CÁC HÀM XỬ LÝ HÓA ĐƠN ---
    public boolean themHoaDon(String maHoaDon, String maPhong, double tongTien) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("maHoaDon", maHoaDon);
        values.put("maPhong", maPhong);
        values.put("tongTien", tongTien);
        long result = db.insert("HoaDon", null, values);
        db.close();
        return result != -1;
    }

    public List<String> getAllHoaDonString() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM HoaDon", null);
        if (cursor.moveToFirst()) {
            do {
                String maHD = cursor.getString(cursor.getColumnIndexOrThrow("maHoaDon"));
                String maP = cursor.getString(cursor.getColumnIndexOrThrow("maPhong"));
                double tong = cursor.getDouble(cursor.getColumnIndexOrThrow("tongTien"));
                list.add("Mã HD: " + maHD + " | Phòng: " + maP + " | Tổng: " + tong + " VNĐ");
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }
}
