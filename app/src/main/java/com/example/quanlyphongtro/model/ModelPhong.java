package com.example.quanlyphongtro.model;

public class ModelPhong {
    private String maPhong;
    private String tenPhong;
    private String giaThue;
    private String trangThai;
    private String tang;
    private String dienTich;

    public ModelPhong(String maPhong, String tenPhong, String giaThue, String trangThai, String tang, String dienTich) {
        this.maPhong = maPhong;
        this.tenPhong = tenPhong;
        this.giaThue = giaThue;
        this.trangThai = trangThai;
        this.tang = tang;
        this.dienTich = dienTich;
    }

    public String getMaPhong() { return maPhong; }
    public void setMaPhong(String maPhong) { this.maPhong = maPhong; }

    public String getTenPhong() { return tenPhong; }
    public void setTenPhong(String tenPhong) { this.tenPhong = tenPhong; }

    public String getGiaThue() { return giaThue; }
    public void setGiaThue(String giaThue) { this.giaThue = giaThue; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getTang() { return tang; }
    public void setTang(String tang) { this.tang = tang; }

    public String getDienTich() { return dienTich; }
    public void setDienTich(String dienTich) { this.dienTich = dienTich; }
}
