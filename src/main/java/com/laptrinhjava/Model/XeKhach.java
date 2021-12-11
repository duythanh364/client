package com.laptrinhjava.Model;

import lombok.Data;

import java.sql.Date;
@Data
public class XeKhach {
    private int id;
    private String bienSo;
    private String mauXe;
    private String hangSX;
    private String model;
    private String doiXe;
    private int soGhe;
    private int soNamSD;
    private Date ngayBaoDuongCuoiCung;
}
