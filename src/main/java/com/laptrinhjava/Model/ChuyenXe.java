package com.laptrinhjava.Model;

import lombok.Data;

import java.sql.Date;
@Data
public class ChuyenXe {
    private int id;
    private int soKhach;
    private double giaVe;
    private Date ngayKhoiHanh;
    private TaiXe laiXe;

    private TaiXe phuXe;

    private TuyenXe tuyenXe;

    private XeKhach xeKhach;
}
