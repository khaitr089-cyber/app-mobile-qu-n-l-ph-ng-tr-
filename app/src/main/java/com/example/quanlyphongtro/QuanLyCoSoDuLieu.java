package com.example.quanlyphongtro;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class QuanLyCoSoDuLieu extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "quanlyphongtro.db";
    private static final int DATABASE_VERSION = 2; // Tăng version để tạo bảng PhongTro

    public QuanLyCoSoDuLieu(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng Người Dùng phục vụ Đăng nhập & Đăng ký
        String sqlUser = "CREATE TABLE NguoiDung (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "tenDangNhap TEXT UNIQUE, " +
                "matKhau TEXT)";
        db.execSQL(sqlUser);
        db.execSQL("INSERT INTO NguoiDung(tenDangNhap, matKhau) VALUES('admin', '123456')");

        // Tạo bảng Phòng Trọ
        String sqlPhong = "CREATE TABLE PhongTro (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "maPhong TEXT UNIQUE, " +
                "tenPhong TEXT, " +
                "giaThue TEXT, " +
                "trangThai TEXT)";
        db.execSQL(sqlPhong);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS NguoiDung");
        db.execSQL("DROP TABLE IF EXISTS PhongTro");
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

    // --- CÁC HÀM XỬ LÝ PHÒNG TRỌ (CRUD) ---
    // 1. Thêm phòng
    public boolean themPhong(String maPhong, String tenPhong, String giaThue, String trangThai) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("maPhong", maPhong);
        values.put("tenPhong", tenPhong);
        values.put("giaThue", giaThue);
        values.put("trangThai", trangThai);
        long result = db.insert("PhongTro", null, values);
        db.close();
        return result != -1;
    }

    // 2. Sửa phòng
    public boolean suaPhong(String maPhong, String tenPhong, String giaThue, String trangThai) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tenPhong", tenPhong);
        values.put("giaThue", giaThue);
        values.put("trangThai", trangThai);
        int rows = db.update("PhongTro", values, "maPhong = ?", new String[]{maPhong});
        db.close();
        return rows > 0;
    }

    // 3. Xóa phòng
    public boolean xoaPhong(String maPhong) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete("PhongTro", "maPhong = ?", new String[]{maPhong});
        db.close();
        return rows > 0;
    }

    // 4. Lấy danh sách phòng trọ
    public List<ModelPhong> getAllPhong() {
        List<ModelPhong> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM PhongTro", null);
        if (cursor.moveToFirst()) {
            do {
                String ma = cursor.getString(cursor.getColumnIndexOrThrow("maPhong"));
                String ten = cursor.getString(cursor.getColumnIndexOrThrow("tenPhong"));
                String gia = cursor.getString(cursor.getColumnIndexOrThrow("giaThue"));
                String tt = cursor.getString(cursor.getColumnIndexOrThrow("trangThai"));
                list.add(new ModelPhong(ma, ten, gia, tt));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }
}
