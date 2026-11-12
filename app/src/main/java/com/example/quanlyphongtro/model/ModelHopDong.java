package com.example.quanlyphongtro.model;

public class ModelHopDong {
    private String maHopDong;
    private String maPhong;
    private String nguoiDaiDien;
    private String nguoiOCung;
    private String ngayBatDau;
    private String ngayKetThuc;
    private double giaThue;
    private double tienCoc;
    private String ngayThanhToanHangThang;
    private String ngayThanhToanDauTien;
    private String ghiChu;
    private String trangThai;

    public ModelHopDong(String maHopDong, String maPhong, String nguoiDaiDien, String nguoiOCung,
                        String ngayBatDau, String ngayKetThuc, double giaThue, double tienCoc,
                        String ngayThanhToanHangThang, String ngayThanhToanDauTien, String ghiChu, String trangThai) {
        this.maHopDong = maHopDong;
        this.maPhong = maPhong;
        this.nguoiDaiDien = nguoiDaiDien;
        this.nguoiOCung = nguoiOCung;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.giaThue = giaThue;
        this.tienCoc = tienCoc;
        this.ngayThanhToanHangThang = ngayThanhToanHangThang;
        this.ngayThanhToanDauTien = ngayThanhToanDauTien;
        this.ghiChu = ghiChu;
        this.trangThai = trangThai;
    }

    public String getMaHopDong() { return maHopDong; }
    public String getMaPhong() { return maPhong; }
    public String getNguoiDaiDien() { return nguoiDaiDien; }
    public String getNguoiOCung() { return nguoiOCung; }
    public String getNgayBatDau() { return ngayBatDau; }
    public String getNgayKetThuc() { return ngayKetThuc; }
    public double getGiaThue() { return giaThue; }
    public double getTienCoc() { return tienCoc; }
    public String getNgayThanhToanHangThang() { return ngayThanhToanHangThang; }
    public String getNgayThanhToanDauTien() { return ngayThanhToanDauTien; }
    public String getGhiChu() { return ghiChu; }
    public String getTrangThai() { return trangThai; }
}
