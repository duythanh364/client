package com.laptrinhjava.Controller;


import com.laptrinhjava.Model.Luong;
import com.laptrinhjava.Model.TaiXe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping(value="/bacluong")
public class BacLuongCtr {
    RestTemplate rest = new RestTemplate();
    @GetMapping
    public String getAll(Model model){
        List<Luong> list = Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/bacluong", Luong[].class));
        model.addAttribute("listBacLuong",list);
        return "bacluong/listBacLuong";
    }
    @GetMapping("/insert")
    public String getInsert(Model model){
        Luong bacluong= new Luong();
        model.addAttribute("bacluong",bacluong);
        return "bacluong/addBacLuong";
    }
    @PostMapping("/insert")
    public String postInsert(Model model, @ModelAttribute Luong bacLuong){
        Luong l = new Luong();
        l=rest.postForObject("https://transportnhom14server.herokuapp.com/api/bacluong",bacLuong,Luong.class);
        if(l == null){
            model.addAttribute("error", "Hệ số lương đã tồn tại");
            model.addAttribute("bacluong",bacLuong);
            return "bacluong/addBacLuong";
        }
        else
            return "redirect:/bacluong";
    }
    @GetMapping("/update/{id}")
    public String getEdit(Model model, @PathVariable int id){
        Luong bacLuong = rest.getForObject("https://transportnhom14server.herokuapp.com/api/bacluong/{id}", Luong.class, id);
        model.addAttribute("bacluong",bacLuong);
        return "bacluong/editBacLuong";
    }
    @PostMapping("/update/{id}")
    public String postEdit(Model model, @ModelAttribute Luong bacLuong ){
        rest.put("https://transportnhom14server.herokuapp.com/api/bacluong",bacLuong);
        return "redirect:/bacluong";
    }

    @GetMapping("/delete/{id}")
    public String delete(Model model, @PathVariable int id){
        List<TaiXe> listTaiXe= Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/taixe/luong/{id}",TaiXe[].class,id));
        if(listTaiXe.size()!=0){
            for(TaiXe c : listTaiXe){
                rest.delete("https://transportnhom14server.herokuapp.com/api/taixe/{id}",c.getId());
            }
        }
        rest.delete("https://transportnhom14server.herokuapp.com/api/bacluong/{id}", id);
        List<Luong> list = Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/bacluong", Luong[].class));
        model.addAttribute("listBacLuong",list);
        return "bacluong/listBacLuong";
    }
}
