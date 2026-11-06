package com.example.quanlyphongtro;

public class ModelHoaDon {
    private String maHoaDon;
    private String maPhong;
    private double tongTien;

    public ModelHoaDon(String maHoaDon, String maPhong, double tongTien) {
        this.maHoaDon = maHoaDon;
        this.maPhong = maPhong;
        this.tongTien = tongTien;
    }

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public String getMaPhong() {
        return maPhong;
    }

    public double getTongTien() {
        return tongTien;
    }
}
