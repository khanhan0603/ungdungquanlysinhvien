package vn.edu.stu.luongminhkhanhan_dh52200299.model;

import java.io.Serializable;

public class Sinhvien implements Serializable {
    private int masv;
    private String ten;
    private int phanloai;
    private byte[] hinhanh;
    private double diem;
    private String sothich;

    private Lop lop;

    public Sinhvien() {
    }

    public Sinhvien(String ten, int phanloai, byte[] hinhanh, double diem, String sothich) {
        this.ten = ten;
        this.phanloai = phanloai;
        this.hinhanh = hinhanh;
        this.diem = diem;
        this.sothich = sothich;
    }

    public Sinhvien(int masv, String ten, int phanloai, byte[] hinhanh, double diem, String sothich) {
        this.masv = masv;
        this.ten = ten;
        this.phanloai = phanloai;
        this.hinhanh = hinhanh;
        this.diem = diem;
        this.sothich = sothich;
    }

    public int getMasv() {
        return masv;
    }

    public void setMasv(int masv) {
        this.masv = masv;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public int getPhanloai() {
        return phanloai;
    }

    public void setPhanloai(int phanloai) {
        this.phanloai = phanloai;
    }

    public byte[] getHinhanh() {
        return hinhanh;
    }

    public void setHinhanh(byte[] hinhanh) {
        this.hinhanh = hinhanh;
    }

    public double getDiem() {
        return diem;
    }

    public void setDiem(double diem) {
        this.diem = diem;
    }

    public String getSothich() {
        return sothich;
    }

    public void setSothich(String sothich) {
        this.sothich = sothich;
    }

    public Lop getLop() {
        return lop;
    }

    public void setLop(Lop lop) {
        this.lop = lop;
        this.phanloai=lop.getMalop();
    }
}
