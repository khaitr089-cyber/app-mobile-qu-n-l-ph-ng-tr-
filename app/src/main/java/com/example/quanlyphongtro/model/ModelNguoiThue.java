package com.example.quanlyphongtro.model;

public class ModelNguoiThue {
    private String hoTen;
    private String soDienThoai;
    private String cccd;
    private String phongThue;
    private String queQuan;
    private String ngaySinh;
    private String ngayChuyenVao;
    private String ngayChuyenDi;
    private String soLuongNguoi;

    public ModelNguoiThue(String hoTen, String soDienThoai, String cccd, String phongThue, String queQuan, String ngaySinh, String ngayChuyenVao, String ngayChuyenDi, String soLuongNguoi) {
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.cccd = cccd;
        this.phongThue = phongThue;
        this.queQuan = queQuan;
        this.ngaySinh = ngaySinh;
        this.ngayChuyenVao = ngayChuyenVao;
        this.ngayChuyenDi = ngayChuyenDi;
        this.soLuongNguoi = soLuongNguoi;
    }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public String getCccd() { return cccd; }
    public void setCccd(String cccd) { this.cccd = cccd; }

    public String getPhongThue() { return phongThue; }
    public void setPhongThue(String phongThue) { this.phongThue = phongThue; }

    public String getQueQuan() { return queQuan; }
    public void setQueQuan(String queQuan) { this.queQuan = queQuan; }

    public String getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(String ngaySinh) { this.ngaySinh = ngaySinh; }

    public String getNgayChuyenVao() { return ngayChuyenVao; }
    public void setNgayChuyenVao(String ngayChuyenVao) { this.ngayChuyenVao = ngayChuyenVao; }

    public String getNgayChuyenDi() { return ngayChuyenDi; }
    public void setNgayChuyenDi(String ngayChuyenDi) { this.ngayChuyenDi = ngayChuyenDi; }

    public String getSoLuongNguoi() { return soLuongNguoi; }
    public void setSoLuongNguoi(String soLuongNguoi) { this.soLuongNguoi = soLuongNguoi; }
}
