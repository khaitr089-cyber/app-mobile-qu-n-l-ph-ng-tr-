package com.example.quanlyphongtro;

import java.net.URLEncoder;

public class VietQRHelper {

    // Hàm tạo đường dẫn ảnh VietQR động chuẩn Napas247
    public static String taoUrlVietQR(String bankId, String accountNumber, String accountName, double amount, String addInfo) {
        try {
            String encodedName = URLEncoder.encode(accountName, "UTF-8");
            String encodedInfo = URLEncoder.encode(addInfo, "UTF-8");
            long amountLong = (long) amount;

            return String.format(
                "https://img.vietqr.io/image/%s-%s-compact2.png?amount=%d&addInfo=%s&accountName=%s",
                bankId.toLowerCase(),
                accountNumber,
                amountLong,
                encodedInfo,
                encodedName
            );
        } catch (Exception e) {
            return "";
        }
    }
}
