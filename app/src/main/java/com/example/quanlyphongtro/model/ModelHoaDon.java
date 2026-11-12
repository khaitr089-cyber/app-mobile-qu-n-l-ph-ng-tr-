package com.example.quanlyphongtro.model;

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

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public String getMaPhong() {
        return maPhong;
    }

    public void setMaPhong(String maPhong) {
        this.maPhong = maPhong;
    }

    public double getTongTien() {
        return tongTien;
    }

    public void setTongTien(double tongTien) {
        this.tongTien = tongTien;
    }
}
