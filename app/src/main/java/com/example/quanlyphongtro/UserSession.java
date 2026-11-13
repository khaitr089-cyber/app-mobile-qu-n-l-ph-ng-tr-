package com.example.quanlyphongtro;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSession {
    // Lưu trữ tài khoản của người dùng đang đăng nhập hiện tại
    public static String currentUser = "admin";

    public static void saveUser(Context context, String username) {
        currentUser = username;
        SharedPreferences pref = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        pref.edit().putString("saved_username", username).apply();
    }

    public static String getUser(Context context) {
        if (currentUser == null || currentUser.isEmpty() || currentUser.equals("admin")) {
            SharedPreferences pref = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            currentUser = pref.getString("saved_username", "admin");
        }
        return currentUser;
    }
}
