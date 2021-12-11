package com.laptrinhjava.Controller;

import com.laptrinhjava.Model.TuyenXe;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping(value="/tuyenxe")
public class TuyenXeCtr {
    RestTemplate rest= new RestTemplate();

    List<TuyenXe> listTuyenXeDefault = new ArrayList<>(); // tuyen xe mac dinh
    List<TuyenXe> listTuyenXeSort = new ArrayList<>(); //tuyen xe xep roi

    @GetMapping
    public String getAll(Model model){
        listTuyenXeDefault = Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/tuyenxe", TuyenXe[].class));
        //Arrays.asList ép kiểu về list;;;         List<TuyenXe> list = Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/tuyenxe", TuyenXe[].class)); dạng json

        listTuyenXeDefault.sort(Comparator.comparing(TuyenXe::getId));

        listTuyenXeSort = new ArrayList<>(listTuyenXeDefault);
        model.addAttribute("listTuyenXe",listTuyenXeDefault);
        return "tuyenxe/listTuyenXe";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") int id){
        rest.delete("https://transportnhom14server.herokuapp.com/api/tuyenxe/{id}", id);
        return "redirect:/tuyenxe";
    }

    @GetMapping("/insert")
    public String getInsert(TuyenXe tuyenXe){
        return "tuyenxe/addTuyenXe";
    }
    @PostMapping("/insert")
    public String postInsert(@ModelAttribute TuyenXe tuyenXe,Model model){
        String diemDau = tuyenXe.getDiemDau();
        String diemCuoi = tuyenXe.getDiemCuoi();
        List<TuyenXe> listTuyenXe =  Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/tuyenxe/search/{diemDau}/{diemCuoi}", TuyenXe[].class,diemDau,diemCuoi));
        if(!listTuyenXe.isEmpty()){
            model.addAttribute("error", "Tuyến xe đã tồn tại");
            model.addAttribute("tuyenxe",tuyenXe);
            return "tuyenxe/addTuyenXe";
        }
        TuyenXe tx= rest.postForObject("https://transportnhom14server.herokuapp.com/api/tuyenxe",tuyenXe,TuyenXe.class);
        // thêm
//        if(tx==null){
//            model.addAttribute("error", "Tuyến xe thêm");
//            model.addAttribute("tuyenxe",tuyenXe);
//            return "tuyenxe/addTuyenXe";
//        }
        return "redirect:/tuyenxe";
    }


    @GetMapping("/update")
    public String getUpdate(@RequestParam("id") int id, Model model){
        TuyenXe tuyenXe=rest.getForObject("https://transportnhom14server.herokuapp.com/api/tuyenxe/{id}", TuyenXe.class,id);
        model.addAttribute(tuyenXe);
        return "tuyenxe/editTuyenXe";
    }
    @PostMapping("/update")
    public String postUpdate(@ModelAttribute TuyenXe tuyenXe){
        rest.put("https://transportnhom14server.herokuapp.com/api/tuyenxe",tuyenXe);
        return "redirect:/tuyenxe";
    }

    @GetMapping("/search")
    public String getSearch(@RequestParam("diemDau") String diemDau,@RequestParam("diemCuoi") String diemCuoi, Model model){
        if(diemDau.equals("")) diemDau=" ";
        if(diemCuoi.equals("")) diemCuoi=" ";
        listTuyenXeDefault =  Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/tuyenxe/search/{diemDau}/{diemCuoi}", TuyenXe[].class,diemDau,diemCuoi));
        model.addAttribute("listTuyenXe",listTuyenXeDefault);
        listTuyenXeSort = new ArrayList<>(listTuyenXeDefault);
        return "tuyenxe/listTuyenXe";
    }

    @GetMapping("/sort")
    public String sort(@RequestParam String sort__type, Model model){
        if(sort__type.equals("Default")){
            listTuyenXeSort.sort(Comparator.comparing(TuyenXe::getId));
        }
        else if(sort__type.equals("diemDau")){
            listTuyenXeSort.sort(Comparator.comparing(TuyenXe::getDiemDau));
        }
        else if(sort__type.equals("diemCuoi")){
            listTuyenXeSort.sort(Comparator.comparing(TuyenXe::getDiemCuoi));
        }
        else if(sort__type.equals("doDai")){
            listTuyenXeSort.sort(Comparator.comparing(TuyenXe::getDoDai));
        }
        else if(sort__type.equals("doPhucTap")){
            listTuyenXeSort.sort(Comparator.comparing(TuyenXe::getDoPhucTap));
        }
        model.addAttribute("listTuyenXe",listTuyenXeSort);
        return "tuyenxe/listTuyenXe";
    }
}
