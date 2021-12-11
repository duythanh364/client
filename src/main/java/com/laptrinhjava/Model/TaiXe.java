package com.laptrinhjava.Model;

import lombok.Data;

import java.sql.Date;
@Data
public class TaiXe{
    private int id;
    private String ten;
    private String CMT;
    private String maSoBangLai;
    private String loaiBangLai;
    private String diaChi;
    private Date ngaySinh;
    private int thamNien;
    private Luong luong;

}
