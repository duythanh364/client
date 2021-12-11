package com.laptrinhjava.Controller;

import com.laptrinhjava.Model.ChuyenXe;
import com.laptrinhjava.Model.Luong;
import com.laptrinhjava.Model.TaiXe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping(value="/taixe")
public class TaiXeCtr {
    RestTemplate rest= new RestTemplate();
    private int currentLuongMonth,currentLuongYear,currentChuyenMonth,currentChuyenYear;

    @GetMapping
    public String getAll(Model model){
        List<TaiXe> list = Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/taixe", TaiXe[].class));

        list.sort(Comparator.comparing((TaiXe taiXe) -> taiXe.getId()));

        model.addAttribute("listTaiXe",list);
        return "taixe/listTaiXe";
    }

    @GetMapping("/insert")
    public String getInsert(Model model){
        TaiXe taixe = new TaiXe();
        List<Luong> listLuong= Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/bacluong", Luong[].class));
        model.addAttribute("taixe",taixe);
        model.addAttribute("listLuong",listLuong);
        return "taixe/addTaiXe";
    }

    @PostMapping("/insert")
    public String postInsert(Model model, @ModelAttribute TaiXe taixe,
                             @RequestParam("bacluong") int idBacLuong ){

        Luong l = rest.getForObject("https://transportnhom14server.herokuapp.com/api/bacluong/{id}", Luong.class, idBacLuong);
        taixe.setLuong(l);

        TaiXe t = new TaiXe();
        t=rest.postForObject("https://transportnhom14server.herokuapp.com/api/taixe",taixe,TaiXe.class);
        if(t == null){
            model.addAttribute("error", "Tài xế đã tồn tại");
            model.addAttribute("taixe",taixe);
            System.out.println(taixe);
            List<Luong> listLuong= Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/bacluong", Luong[].class));
            model.addAttribute("listLuong",listLuong);
            return "taixe/addTaiXe";
        }
        else
        return "redirect:/taixe";
    }
    @GetMapping("/update/{id}")
    public String getEdit(Model model, @PathVariable int id){
        TaiXe taixe = rest.getForObject("https://transportnhom14server.herokuapp.com/api/taixe/{id}", TaiXe.class, id);
        List<Luong> listLuong= Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/bacluong", Luong[].class));
        model.addAttribute("taixe",taixe);
        model.addAttribute("listLuong",listLuong);
        return "taixe/editTaiXe";
    }
    @PostMapping("/update/{id}")
    public String postEdit(Model model, @ModelAttribute TaiXe taixe,
                             @RequestParam("bacluong") int idBacLuong ){
        Luong l = rest.getForObject("https://transportnhom14server.herokuapp.com/api/bacluong/{id}", Luong.class, idBacLuong);
        taixe.setLuong(l);
        rest.put("https://transportnhom14server.herokuapp.com/api/taixe",taixe);
        return "redirect:/taixe";
    }

    //delete by tai xe with chuyen xe
    @GetMapping("/delete/{id}")
    public String getDeleteWithChuyenXe(Model model, @PathVariable int id){
        List<ChuyenXe> listChuyenXe= Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/chuyenxe/taixe/{id}",ChuyenXe[].class,id));
        if(listChuyenXe.size()!=0){
            for(ChuyenXe c : listChuyenXe){
                rest.delete("https://transportnhom14server.herokuapp.com/api/chuyenxe/{id}",c.getId());
            }
        }
        rest.delete("https://transportnhom14server.herokuapp.com/api/taixe/{id}", id);
        List<TaiXe> list = Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/taixe", TaiXe[].class));
        model.addAttribute("listTaiXe",list);
        return "taixe/listTaiXe";
    }

    @GetMapping("/search")
    public String getSearch(Model model, @RequestParam("keyword") String key,
            @RequestParam("search__type") String type){
        List<TaiXe> list = Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/taixe/search/{search__type}/{key}",TaiXe[].class,type,key));
        if(list.size() != 0) {
            model.addAttribute("listTaiXe",list);
        }
        else  {
            model.addAttribute("error", "Không tìm thấy tài xế");
        }
        return "taixe/listTaiXe";
    }
    @GetMapping("/luongthang")
    public String luongThangHienTai( Model model){
        // Phuc vu cho ham tim kiem
        LocalDate todaydate = LocalDate.now();
        currentLuongMonth=todaydate.getMonthValue();
        currentLuongYear=todaydate.getYear();
        DecimalFormat df = new DecimalFormat("#.##");
        df.setMaximumFractionDigits(340);
        // tinh luong trong thang
        Map<String,Integer> mapLaiXe= rest.getForObject("https://transportnhom14server.herokuapp.com/api/thongke/sochuyenlaixe/{month}/{year}",Map.class,currentLuongMonth,currentLuongYear);
        Map<String,Integer> mapPhuXe= rest.getForObject("https://transportnhom14server.herokuapp.com/api/thongke/sochuyenphuxe/{month}/{year}",Map.class,currentLuongMonth,currentLuongYear);
        Map<String,Double> mapLuong= rest.getForObject("https://transportnhom14server.herokuapp.com/api/thongke/luongthang/{month}/{year}",Map.class,currentLuongMonth,currentLuongYear);

        Map<Integer, Integer> mapLaiXeConvert= new  HashMap<>();
        Map<Integer, Integer> mapPhuXeConvert= new  HashMap<>();
        Map<Integer, String> mapLuongConvert= new  HashMap<>();

        for(String a : mapLuong.keySet()) mapLuongConvert.put(Integer.parseInt(a),df.format(mapLuong.get(a)));
        for(String a : mapLaiXe.keySet()) mapLaiXeConvert.put(Integer.parseInt(a),mapLaiXe.get(a));
        for(String a : mapPhuXe.keySet()) mapPhuXeConvert.put(Integer.parseInt(a),mapPhuXe.get(a));

        List<TaiXe> list = Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/taixe", TaiXe[].class));
        model.addAttribute("listTaiXe", list);
        model.addAttribute("mapLuong",mapLuongConvert);
        model.addAttribute("mapLaiXe",mapLaiXeConvert);
        model.addAttribute("mapPhuXe",mapPhuXeConvert);

        return "taixe/thongKeLuong";
    }
    @PostMapping("/luongthang")
    public String luongThangKhac(@RequestParam("month") int month,@RequestParam("year") int year,Model model){
        currentLuongMonth=month;
        currentLuongYear=year;
        DecimalFormat df = new DecimalFormat("#.##");
        df.setMaximumFractionDigits(340);
        // tinh luong trong thang
        Map<String,Integer> mapLaiXe= rest.getForObject("https://transportnhom14server.herokuapp.com/api/thongke/sochuyenlaixe/{month}/{year}",Map.class,month,year);
        Map<String,Integer> mapPhuXe= rest.getForObject("https://transportnhom14server.herokuapp.com/api/thongke/sochuyenphuxe/{month}/{year}",Map.class,month,year);
        Map<String,Double> mapLuong= rest.getForObject("https://transportnhom14server.herokuapp.com/api/thongke/luongthang/{month}/{year}",Map.class,month,year);

        Map<Integer, Integer> mapLaiXeConvert= new  HashMap<>();
        Map<Integer, Integer> mapPhuXeConvert= new  HashMap<>();
        Map<Integer, String> mapLuongConvert= new  HashMap<>();

        for(String a : mapLuong.keySet()) mapLuongConvert.put(Integer.parseInt(a),df.format(mapLuong.get(a)));
        for(String a : mapLaiXe.keySet()) mapLaiXeConvert.put(Integer.parseInt(a),mapLaiXe.get(a));
        for(String a : mapPhuXe.keySet()) mapPhuXeConvert.put(Integer.parseInt(a),mapPhuXe.get(a));

        List<TaiXe> list = Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/taixe", TaiXe[].class));
        model.addAttribute("listTaiXe", list);
        model.addAttribute("mapLuong",mapLuongConvert);
        model.addAttribute("mapLaiXe",mapLaiXeConvert);
        model.addAttribute("mapPhuXe",mapPhuXeConvert);
        return "taixe/thongKeLuong";
    }
    @GetMapping("/search/luong")
    public String getSearchLuong(Model model, @RequestParam("keyword") String key,
                            @RequestParam("search__type") String type){

        List<TaiXe> list = Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/taixe/search/{search__type}/{key}",TaiXe[].class,type,key));
        if(list.size() == 0){
            model.addAttribute("error", "Không tìm thấy tài xế");
        }
        else{
            DecimalFormat df = new DecimalFormat("#.##");
            df.setMaximumFractionDigits(340);
            // tinh luong trong thang
            Map<String,Integer> mapLaiXe= rest.getForObject("https://transportnhom14server.herokuapp.com/api/thongke/sochuyenlaixe/{month}/{year}",Map.class,currentLuongMonth,currentLuongYear);
            Map<String,Integer> mapPhuXe= rest.getForObject("https://transportnhom14server.herokuapp.com/api/thongke/sochuyenphuxe/{month}/{year}",Map.class,currentLuongMonth,currentLuongYear);
            Map<String,Double> mapLuong= rest.getForObject("https://transportnhom14server.herokuapp.com/api/thongke/luongthang/{month}/{year}",Map.class,currentLuongMonth,currentLuongYear);

            Map<Integer, Integer> mapLaiXeConvert= new  HashMap<>();
            Map<Integer, Integer> mapPhuXeConvert= new  HashMap<>();
            Map<Integer, String> mapLuongConvert= new  HashMap<>();

            for(String a : mapLuong.keySet()) mapLuongConvert.put(Integer.parseInt(a),df.format(mapLuong.get(a)));
            for(String a : mapLaiXe.keySet()) mapLaiXeConvert.put(Integer.parseInt(a),mapLaiXe.get(a));
            for(String a : mapPhuXe.keySet()) mapPhuXeConvert.put(Integer.parseInt(a),mapPhuXe.get(a));

//            List<TaiXe> list2 = Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/taixe", TaiXe[].class));
            model.addAttribute("listTaiXe", list);
            model.addAttribute("mapLuong",mapLuongConvert);
            model.addAttribute("mapLaiXe",mapLaiXeConvert);
            model.addAttribute("mapPhuXe",mapPhuXeConvert);
        }
        return "taixe/thongKeLuong";
    }

    @GetMapping("/chuyendachay")
    public String statChuyenDaChay(Model model){
        LocalDate todaydate = LocalDate.now();
        currentChuyenMonth= todaydate.getMonthValue();
        currentChuyenYear=todaydate.getYear();
        Map<String, List<ChuyenXe>> mapChuyen= rest.getForObject("https://transportnhom14server.herokuapp.com/api/thongke/chuyendachay/{month}/{year}",Map.class, currentChuyenMonth, currentChuyenYear);

        List<TaiXe> list = Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/taixe", TaiXe[].class));

        model.addAttribute("listTaiXe",list);
        model.addAttribute("mapChuyenDaChay",mapChuyen);
        return "taixe/chuyenDaChay";
    }
    @PostMapping("/chuyendachay")
    public String statChuyenDaChayPost(@RequestParam("month") int month,@RequestParam("year") int year,Model model){

        currentChuyenMonth= month;
        currentChuyenYear=year;
        Map<String, List<ChuyenXe>> mapChuyen= rest.getForObject("https://transportnhom14server.herokuapp.com/api/thongke/chuyendachay/{month}/{year}",Map.class, month, year);

        List<TaiXe> list = Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/taixe", TaiXe[].class));


        model.addAttribute("listTaiXe",list);
        model.addAttribute("mapChuyenDaChay",mapChuyen);
        return "taixe/chuyenDaChay";
    }
    @GetMapping("/search/chuyendachay")
    public String getSearchChuyenDaChay(Model model, @RequestParam("keyword") String key,
                                 @RequestParam("search__type") String type){
        // tim kiem theo thang,nam vua nhap gan nhat
        List<TaiXe> list = Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/taixe/search/{search__type}/{key}",TaiXe[].class,type,key));

        if(list.size() == 0){
            model.addAttribute("error", "Không tìm thấy tài xế");
        }
        else{

            Map<String, List<ChuyenXe>> mapChuyen= rest.getForObject("https://transportnhom14server.herokuapp.com/api/thongke/chuyendachay/{month}/{year}",Map.class, currentChuyenMonth, currentChuyenYear);

            model.addAttribute("listTaiXe",list);
            Map<String, List<ChuyenXe>> mapChuyenNew= new HashMap<>();
            for(TaiXe t : list){
                if(mapChuyen.containsKey(t.getTen())) mapChuyenNew.put(t.getTen(), mapChuyen.get(t.getTen()));
            }

            model.addAttribute("mapChuyenDaChay",mapChuyenNew);
        }
        return "taixe/chuyenDaChay";
    }
    @GetMapping("/sort")
    public String sort(Model model, @RequestParam String sort__type, HttpServletRequest request) {
        List<TaiXe> list = Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/taixe",TaiXe[].class));
        if (sort__type.equals("ten")) {
            list.sort(Comparator.comparing((TaiXe taiXe) -> taiXe.getTen()));
        }
        if (sort__type.equals("thamNien")) {
            list.sort(Comparator.comparing((TaiXe taiXe) -> taiXe.getThamNien()));
        }

        model.addAttribute("listTaiXe", list);
        return "taixe/listTaiXe";

    }
}
