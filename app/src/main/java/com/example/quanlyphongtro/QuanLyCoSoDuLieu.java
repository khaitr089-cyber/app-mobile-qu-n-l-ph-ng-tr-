package com.example.quanlyphongtro;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class QuanLyCoSoDuLieu extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "quanlyphongtro.db";
    private static final int DATABASE_VERSION = 1;

    public QuanLyCoSoDuLieu(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng Người Dùng phục vụ Đăng nhập & Đăng ký
        String sql = "CREATE TABLE NguoiDung (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "tenDangNhap TEXT UNIQUE, " +
                "matKhau TEXT)";
        db.execSQL(sql);

        // Thêm tài khoản mẫu mặc định (admin / 123456)
        db.execSQL("INSERT INTO NguoiDung(tenDangNhap, matKhau) VALUES('admin', '123456')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS NguoiDung");
        onCreate(db);
    }

    // Hàm kiểm tra Đăng nhập
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

    // Hàm Đăng ký tài khoản mới
    public boolean dangky(String taiKhoan, String matKhau) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tenDangNhap", taiKhoan);
        values.put("matKhau", matKhau);
        long result = db.insert("NguoiDung", null, values);
        db.close();
        return result != -1;
    }
}
