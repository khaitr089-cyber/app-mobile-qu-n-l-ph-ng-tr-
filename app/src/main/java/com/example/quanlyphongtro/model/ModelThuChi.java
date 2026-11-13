package com.example.quanlyphongtro.model;

public class ModelThuChi {
    private int id;
    private String loai; // "THU" hoặc "CHI"
    private double soTien;
    private String danhMuc; // "Bảo trì", "Sửa chữa", "Tiền rác", "Wifi", "Khác"
    private String ghiChu;
    private String ngayGhi;

    public ModelThuChi(int id, String loai, double soTien, String danhMuc, String ghiChu, String ngayGhi) {
        this.id = id;
        this.loai = loai;
        this.soTien = soTien;
        this.danhMuc = danhMuc;
        this.ghiChu = ghiChu;
        this.ngayGhi = ngayGhi;
    }

    public int getId() { return id; }
    public String getLoai() { return loai; }
    public double getSoTien() { return soTien; }
    public String getDanhMuc() { return danhMuc; }
    public String getGhiChu() { return ghiChu; }
    public String getNgayGhi() { return ngayGhi; }
}
