package vn.edu.stu.luongminhkhanhan_dh52200299.model;

import java.io.Serializable;

public class Lop implements Serializable {
    private int malop;
    private String tenlop;

    public Lop() {
    }

    public Lop(String tenlop) {
        this.tenlop = tenlop;
    }

    public Lop(int malop, String tenlop) {
        this.malop = malop;
        this.tenlop = tenlop;
    }

    public int getMalop() {
        return malop;
    }

    public void setMalop(int malop) {
        this.malop = malop;
    }

    public String getTenlop() {
        return tenlop;
    }

    public void setTenlop(String tenlop) {
        this.tenlop = tenlop;
    }

    @Override
    public String toString() {
        // return "Mã lớp: "+malop+" - "+"Tên lớp: "+tenlop;
        return tenlop;
    }
}