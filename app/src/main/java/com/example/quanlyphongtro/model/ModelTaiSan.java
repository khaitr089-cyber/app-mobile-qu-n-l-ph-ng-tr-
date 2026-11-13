package com.example.quanlyphongtro.model;

public class ModelTaiSan {
    private int id;
    private String maPhong;
    private String tenTaiSan;
    private String trangThai; // "Mới 100%", "Hoạt động tốt", "Hỏng hóc", "Đã thanh lý"
    private double giaTri;

    public ModelTaiSan(int id, String maPhong, String tenTaiSan, String trangThai, double giaTri) {
        this.id = id;
        this.maPhong = maPhong;
        this.tenTaiSan = tenTaiSan;
        this.trangThai = trangThai;
        this.giaTri = giaTri;
    }

    public int getId() { return id; }
    public String getMaPhong() { return maPhong; }
    public String getTenTaiSan() { return tenTaiSan; }
    public String getTrangThai() { return trangThai; }
    public double getGiaTri() { return giaTri; }
}
