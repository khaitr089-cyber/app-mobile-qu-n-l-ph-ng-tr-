package com.example.quanlyphongtro.model;

public class ModelDienNuoc {
    private String maPhong;
    private String thangGhi;
    private double dienCu;
    private double dienMoi;
    private double nuocCu;
    private double nuocMoi;
    private double tienDichVu;
    private double tongTien;

    public ModelDienNuoc(String maPhong, String thangGhi, double dienCu, double dienMoi, double nuocCu, double nuocMoi, double tienDichVu, double tongTien) {
        this.maPhong = maPhong;
        this.thangGhi = thangGhi;
        this.dienCu = dienCu;
        this.dienMoi = dienMoi;
        this.nuocCu = nuocCu;
        this.nuocMoi = nuocMoi;
        this.tienDichVu = tienDichVu;
        this.tongTien = tongTien;
    }

    public String getMaPhong() { return maPhong; }
    public String getThangGhi() { return thangGhi; }
    public double getDienCu() { return dienCu; }
    public double getDienMoi() { return dienMoi; }
    public double getNuocCu() { return nuocCu; }
    public double getNuocMoi() { return nuocMoi; }
    public double getTienDichVu() { return tienDichVu; }
    public double getTongTien() { return tongTien; }
}
