package com.example.quanlyphongtro;

public class ModelNguoiThue {
    private String hoTen;
    private String soDienThoai;
    private String cccd;
    private String phongThue;
    private String queQuan;

    public ModelNguoiThue(String hoTen, String soDienThoai, String cccd, String phongThue, String queQuan) {
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.cccd = cccd;
        this.phongThue = phongThue;
        this.queQuan = queQuan;
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
}
