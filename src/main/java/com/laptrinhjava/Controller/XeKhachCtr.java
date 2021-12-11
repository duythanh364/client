package com.laptrinhjava.Controller;

import com.laptrinhjava.Model.ChuyenXe;
import com.laptrinhjava.Model.XeKhach;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping(value = "/xekhach")
public class XeKhachCtr {

    RestTemplate rest = new RestTemplate();

    List<XeKhach> xeKhachDefault;
    List<XeKhach> listXeKhach;

    @GetMapping
    public String getAll(Model model) {
        List<XeKhach> list = Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/xekhach",
                XeKhach[].class));

        list.sort(Comparator.comparing((XeKhach xeKhach) -> xeKhach.getId()));

        model.addAttribute("listXeKhach", list);
        xeKhachDefault = list;
        listXeKhach = list;
        return "xekhach/listXeKhach";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") int id) {
        rest.delete("https://transportnhom14server.herokuapp.com/api/xekhach/{id}", id);
        return "redirect:/xekhach";
    }

    @GetMapping("/insert")
    public String getInsert(XeKhach xeKhach) {
        return "xekhach/addXeKhach";
    }

    @PostMapping("/insert")
    public String postInsert(@ModelAttribute XeKhach xeKhach, Model model) {
        XeKhach xk = rest.postForObject("https://transportnhom14server.herokuapp.com/api/xekhach",
                xeKhach, XeKhach.class);
        if (xk == null) {
            model.addAttribute("error", "Xe khach đã tồn tại");
            model.addAttribute("xekhach", xk);
            return "xekhach/addXeKhach";
        }
        System.out.println(xk);
        return "redirect:/xekhach";
    }

    @GetMapping("/update")
    public String getUpdate(@RequestParam int id, Model model) {
        XeKhach xeKhach = rest.getForObject("https://transportnhom14server.herokuapp.com/api/xekhach/{id}",
                XeKhach.class, id);
        model.addAttribute(xeKhach);
        return "xekhach/editXeKhach";
    }

    @PostMapping("/update")
    public String postUpdate(@ModelAttribute XeKhach xeKhach, Model model) {
        rest.put("https://transportnhom14server.herokuapp.com/api/xekhach", xeKhach);
        return "redirect:/xekhach";
    }

    @GetMapping("/thongke")
    public String getThongKe(Model model) {
        HashMap<XeKhach, Double> mapDoanhThu = new HashMap<>();
        HashMap<XeKhach, String> mapDoanhThuConvert = new HashMap<>();
        HashMap<XeKhach, Integer> mapSoChuyen = new HashMap<>();
        List<ChuyenXe> chuyenXes = Arrays.asList(rest.getForObject(
                "https://transportnhom14server.herokuapp.com/api/chuyenxe",
                ChuyenXe[].class));
        DecimalFormat df = new DecimalFormat("#.##");
        df.setMaximumFractionDigits(340); // 340 = DecimalFormat.DOUBLE_FRACTION_DIGITS
        for (ChuyenXe chuyenXe : chuyenXes) {
            if (mapDoanhThu.containsKey(chuyenXe.getXeKhach())) {
                mapDoanhThu.put(chuyenXe.getXeKhach(), mapDoanhThu.get(chuyenXe.getXeKhach())
                        + chuyenXe.getGiaVe() * chuyenXe.getSoKhach());
                mapSoChuyen.put(chuyenXe.getXeKhach()
                        , mapSoChuyen.get(chuyenXe.getXeKhach())+1);
            } else {
                mapDoanhThu.put(chuyenXe.getXeKhach(),
                        chuyenXe.getGiaVe() * chuyenXe.getSoKhach());
                mapSoChuyen.put(chuyenXe.getXeKhach()
                        ,1);
            }
            mapDoanhThuConvert.put(chuyenXe.getXeKhach(),df.format(mapDoanhThu.get(chuyenXe.getXeKhach())));
        }

        model.addAttribute("mapDoanhThu", mapDoanhThuConvert);
        model.addAttribute("mapSoChuyen", mapSoChuyen);
        model.addAttribute("start", LocalDate.now());
        model.addAttribute("end", LocalDate.now());
        return "xekhach/thongKeDoanhThuXeKhach";
    }

    @PostMapping("/thongke")
    public String postThongKe(@RequestParam("start") String start,
                              @RequestParam("end") String end,
                              Model model) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date startDate = new Date(format.parse(start).getTime());
            Date endDate = new Date(format.parse(end).getTime());

            HashMap<XeKhach, Double> mapDoanhThu = new HashMap<>();
            HashMap<XeKhach, String> mapDoanhThuConvert = new HashMap<>();
            HashMap<XeKhach, Integer> mapSoChuyen = new HashMap<>();
            model.addAttribute("start", startDate);
            model.addAttribute("end", endDate);

            if (startDate.after(endDate)) {
                model.addAttribute("error", "Ngày bắt đầu(" + start + ") "
                        + "phải trước ngày kết thúc(" + end + ").");

                model.addAttribute("mapDoanhThu", mapDoanhThu);
                model.addAttribute("mapSoChuyen", mapSoChuyen);
            } else {

                List<ChuyenXe> chuyenXes = Arrays.asList(rest.getForObject(
                        "https://transportnhom14server.herokuapp.com/api/chuyenxe/{start}/{end}",
                        ChuyenXe[].class, startDate, endDate));
                DecimalFormat df = new DecimalFormat("#.##");
                df.setMaximumFractionDigits(340); // 340 = DecimalFormat.DOUBLE_FRACTION_DIGITS
                for (ChuyenXe chuyenXe : chuyenXes) {
                    if (mapDoanhThu.containsKey(chuyenXe.getXeKhach())) {
                        mapDoanhThu.put(chuyenXe.getXeKhach(), mapDoanhThu.get(chuyenXe.getXeKhach())
                                + chuyenXe.getGiaVe() * chuyenXe.getSoKhach());
                        mapSoChuyen.put(chuyenXe.getXeKhach()
                                , mapSoChuyen.get(chuyenXe.getXeKhach())+1);
                    } else {
                        mapDoanhThu.put(chuyenXe.getXeKhach(),
                                chuyenXe.getGiaVe() * chuyenXe.getSoKhach());
                        mapSoChuyen.put(chuyenXe.getXeKhach()
                                ,1);
                    }
                    mapDoanhThuConvert.put(chuyenXe.getXeKhach(),df.format(mapDoanhThu.get(chuyenXe.getXeKhach())));
                }
                model.addAttribute("message", "Thống kê từ "+ start + " đến " + end + ".");
                model.addAttribute("mapDoanhThu", mapDoanhThuConvert);
                model.addAttribute("mapSoChuyen", mapSoChuyen);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "xekhach/thongKeDoanhThuXeKhach";
    }

    @GetMapping("/search")
    public String getSearch(@RequestParam("search__type") String search__type,
                            @RequestParam("keyword") String keyword, Model model) {

        List<XeKhach> list = new ArrayList<>();
        // check validate
        if (search__type.equals("ID")) {
            try {
                int id = Integer.parseInt(keyword);
                XeKhach xeKhach = rest.getForObject("https://transportnhom14server.herokuapp.com/api/xekhach/{id}",
                        XeKhach.class, id);
                if (xeKhach != null) {
                    list.add(xeKhach);
                }
            } catch (NumberFormatException e) {
                model.addAttribute("error", "Nhập ID là 1 số nguyên");
                return "forward:/xekhach";
            }
        } else {
            if (search__type.equals("soGhe")) {
                try {
                    Integer.parseInt(keyword);
                } catch (Exception e) {
                    model.addAttribute("error", "Nhập Số Ghế là 1 số nguyên");
                    System.out.println("YES");
                    return "forward:/xekhach";
                }
            }
            if (search__type.equals("soNamSD")) {
                try {
                    Integer.parseInt(keyword);
                } catch (Exception e) {
                    model.addAttribute("error", "Nhập Số Năm Sử Dụng là 1 số nguyên");
                    System.out.println("YES");
                    return "forward:/xekhach";
                }
            }
            if (keyword.equals("") || search__type.equals("")) {
                model.addAttribute("error", "Search Box trống");
                return "forward:/xekhach";
            }

            list = Arrays.asList(rest.getForObject(
                    "https://transportnhom14server.herokuapp.com/api/xekhach/search/{search__type}/{keyword}",
                    XeKhach[].class, search__type, keyword));

            if (list == null) {
                return "forward:/xekhach";
            }
        }
        model.addAttribute("listXeKhach", list);
        model.addAttribute("keyword", keyword);
        model.addAttribute("message","Searched by " + search__type);
        xeKhachDefault = new ArrayList<>(list);
        listXeKhach = new ArrayList<>(list);

        return "xekhach/listXeKhach";
    }

    @GetMapping("/sort")
    public String sort(Model model, @RequestParam String sort__type, HttpServletRequest request) {

        if (sort__type.equals("bienSo")) {
            listXeKhach.sort(Comparator.comparing((XeKhach xeKhach) -> xeKhach.getBienSo()));
        }
        if (sort__type.equals("doiXe")) {
            listXeKhach.sort(Comparator.comparing((XeKhach xeKhach) -> xeKhach.getDoiXe()));
        }
        if (sort__type.equals("hangSX")) {
            listXeKhach.sort(Comparator.comparing((XeKhach xeKhach) -> xeKhach.getHangSX()));
        }
        if (sort__type.equals("mauXe")) {
            listXeKhach.sort(Comparator.comparing((XeKhach xeKhach) -> xeKhach.getMauXe()));
        }
        if (sort__type.equals("model")) {
            listXeKhach.sort(Comparator.comparing((XeKhach xeKhach) -> xeKhach.getModel()));
        }
        if (sort__type.equals("ngayBaoDuongCuoiCung")) {
            listXeKhach.sort(Comparator.comparing((XeKhach xeKhach) -> xeKhach.getNgayBaoDuongCuoiCung()));
        }
        if (sort__type.equals("soGhe")) {
            listXeKhach.sort(Comparator.comparing((XeKhach xeKhach) -> xeKhach.getSoGhe()));
        }
        if (sort__type.equals("soNamSD")) {
            listXeKhach.sort(Comparator.comparing((XeKhach xeKhach) -> xeKhach.getSoNamSD()));
        }
        if (sort__type.equals("default")) {
            listXeKhach.sort(Comparator.comparing((XeKhach xeKhach) -> xeKhach.getId()));
        }

        model.addAttribute("listXeKhach", listXeKhach);
        model.addAttribute("message","Sorted by " + sort__type);
        return "xekhach/listXeKhach";

    }

}
