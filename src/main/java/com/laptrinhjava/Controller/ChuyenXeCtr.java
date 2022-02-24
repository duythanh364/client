package com.laptrinhjava.Controller;

import com.laptrinhjava.Model.ChuyenXe;
import com.laptrinhjava.Model.TaiXe;
import com.laptrinhjava.Model.TuyenXe;
import com.laptrinhjava.Model.XeKhach;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
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
@RequestMapping(value = "/chuyenxe")
public class ChuyenXeCtr {

    RestTemplate rest = new RestTemplate();

    List<ChuyenXe> chuyenXeDefault;
    List<ChuyenXe> listChuyenXe;

    @GetMapping
    public String getAll(Model model) {
        List<ChuyenXe> list = Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/chuyenxe", ChuyenXe[].class));

        list.sort(Comparator.comparing((ChuyenXe chuyenXe) -> chuyenXe.getId()));

        chuyenXeDefault = new ArrayList<>(list);
        listChuyenXe=new ArrayList<>(list);
        model.addAttribute("listChuyenXe", list);
        return "chuyenxe/listChuyenXe";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") int id) {
        rest.delete("https://transportnhom14server.herokuapp.com/api/chuyenxe/{id}", id);
        return "redirect:/chuyenxe";
    }

    @GetMapping("/insert")
    public String getInsert(Model model, ChuyenXe chuyenXe) {

        List<TaiXe> listTaiXe = Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/taixe", TaiXe[].class));
        List<XeKhach> listXeKhach = Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/xekhach", XeKhach[].class));
        List<TuyenXe> listTuyenXe = Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/tuyenxe", TuyenXe[].class));

        model.addAttribute("listTaiXe", listTaiXe);
        model.addAttribute("listXeKhach", listXeKhach);
        model.addAttribute("listTuyenXe", listTuyenXe);

        return "chuyenxe/addChuyenXe";
    }

    @PostMapping("/insert")
    public String postInsert(Model model, @ModelAttribute ChuyenXe chuyenXe,
                             @RequestParam int idTaiXe,
                             @RequestParam int idPhuXe,
                             @RequestParam int idXeKhach,
                             @RequestParam int idTuyenXe) {

        TaiXe taiXe = rest.getForObject("https://transportnhom14server.herokuapp.com/api/taixe/{id}", TaiXe.class, idTaiXe);
        TaiXe phuXe = rest.getForObject("https://transportnhom14server.herokuapp.com/api/taixe/{id}", TaiXe.class, idPhuXe);
        XeKhach xeKhach = rest.getForObject("https://transportnhom14server.herokuapp.com/api/xekhach/{id}", XeKhach.class, idXeKhach);
        TuyenXe tuyenXe = rest.getForObject("https://transportnhom14server.herokuapp.com/api/tuyenxe/{id}", TuyenXe.class, idTuyenXe);

        if(chuyenXe.getSoKhach()>=xeKhach.getSoGhe()-2){
            model.addAttribute("error", "Vượt quá số lượng");
            model.addAttribute("chuyenxe", chuyenXe);

            List<TaiXe> listTaiXe = Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/taixe", TaiXe[].class));
            List<XeKhach> listXeKhach = Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/xekhach", XeKhach[].class));
            List<TuyenXe> listTuyenXe = Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/tuyenxe", TuyenXe[].class));

            model.addAttribute("listTaiXe", listTaiXe);
            model.addAttribute("listXeKhach", listXeKhach);
            model.addAttribute("listTuyenXe", listTuyenXe);

            return "chuyenxe/addChuyenXe";
        }
        if(taiXe.getId()==phuXe.getId()){
            model.addAttribute("error", "Lái xe, phụ xe không được trùng");
            model.addAttribute("chuyenxe", chuyenXe);

            List<TaiXe> listTaiXe = Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/taixe", TaiXe[].class));
            List<XeKhach> listXeKhach = Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/xekhach", XeKhach[].class));
            List<TuyenXe> listTuyenXe = Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/tuyenxe", TuyenXe[].class));

            model.addAttribute("listTaiXe", listTaiXe);
            model.addAttribute("listXeKhach", listXeKhach);
            model.addAttribute("listTuyenXe", listTuyenXe);

            return "chuyenxe/addChuyenXe";
        }
        chuyenXe.setPhuXe(phuXe);
        chuyenXe.setTuyenXe(tuyenXe);
        chuyenXe.setXeKhach(xeKhach);
        chuyenXe.setLaiXe(taiXe);

        System.out.println(chuyenXe);

        ChuyenXe cx = new ChuyenXe();
        cx = rest.postForObject("https://transportnhom14server.herokuapp.com/api/chuyenxe", chuyenXe, ChuyenXe.class);
        if (cx == null) {
            model.addAttribute("error", "Chuyen xe đã tồn tại");
            model.addAttribute("chuyenxe", chuyenXe);

            List<TaiXe> listTaiXe = Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/taixe", TaiXe[].class));
            List<XeKhach> listXeKhach = Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/xekhach", XeKhach[].class));
            List<TuyenXe> listTuyenXe = Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/tuyenxe", TuyenXe[].class));

            model.addAttribute("listTaiXe", listTaiXe);
            model.addAttribute("listXeKhach", listXeKhach);
            model.addAttribute("listTuyenXe", listTuyenXe);

            return "chuyenxe/addChuyenXe";
        }
        return "redirect:/chuyenxe";
    }

    @GetMapping("update")
    public String getUpdate(@RequestParam int id, Model model) {

        ChuyenXe chuyenXe = rest.getForObject("https://transportnhom14server.herokuapp.com/api/chuyenxe/{id}", ChuyenXe.class, id);

        List<TaiXe> listTaiXe = Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/taixe", TaiXe[].class));
        List<XeKhach> listXeKhach = Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/xekhach", XeKhach[].class));
        List<TuyenXe> listTuyenXe = Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/tuyenxe", TuyenXe[].class));

        model.addAttribute(chuyenXe);
        model.addAttribute("listTaiXe", listTaiXe);
        model.addAttribute("listXeKhach", listXeKhach);
        model.addAttribute("listTuyenXe", listTuyenXe);

        return "chuyenxe/editChuyenXe";
    }

    @PostMapping("update")
    public String postUpdate(@ModelAttribute ChuyenXe chuyenXe, Model model,
                             @RequestParam int idTaiXe,
                             @RequestParam int idPhuXe,
                             @RequestParam int idXeKhach,
                             @RequestParam int idTuyenXe
    ) {

        TaiXe taiXe = rest.getForObject("https://transportnhom14server.herokuapp.com/api/taixe/{id}", TaiXe.class, idTaiXe);
        TaiXe phuXe = rest.getForObject("https://transportnhom14server.herokuapp.com/api/taixe/{id}", TaiXe.class, idPhuXe);
        XeKhach xeKhach = rest.getForObject("https://transportnhom14server.herokuapp.com/api/xekhach/{id}", XeKhach.class, idXeKhach);
        TuyenXe tuyenXe = rest.getForObject("https://transportnhom14server.herokuapp.com/api/tuyenxe/{id}", TuyenXe.class, idTuyenXe);

        if(chuyenXe.getSoKhach()>=xeKhach.getSoGhe()-2){
            model.addAttribute("error", "Vượt quá số lượng");
            model.addAttribute("chuyenxe", chuyenXe);

            List<TaiXe> listTaiXe = Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/taixe", TaiXe[].class));
            List<XeKhach> listXeKhach = Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/xekhach", XeKhach[].class));
            List<TuyenXe> listTuyenXe = Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/tuyenxe", TuyenXe[].class));

            model.addAttribute("listTaiXe", listTaiXe);
            model.addAttribute("listXeKhach", listXeKhach);
            model.addAttribute("listTuyenXe", listTuyenXe);

            return "chuyenxe/editChuyenXe";
        }
        if(taiXe.getId()==phuXe.getId()){
            model.addAttribute("error", "Lái xe, phụ xe không được trùng");
            model.addAttribute("chuyenxe", chuyenXe);

            List<TaiXe> listTaiXe = Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/taixe", TaiXe[].class));
            List<XeKhach> listXeKhach = Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/xekhach", XeKhach[].class));
            List<TuyenXe> listTuyenXe = Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/tuyenxe", TuyenXe[].class));

            model.addAttribute("listTaiXe", listTaiXe);
            model.addAttribute("listXeKhach", listXeKhach);
            model.addAttribute("listTuyenXe", listTuyenXe);
            // day 1 loi
            return "chuyenxe/editChuyenXe";
        }
        chuyenXe.setPhuXe(phuXe);
        chuyenXe.setTuyenXe(tuyenXe);
        chuyenXe.setXeKhach(xeKhach);
        chuyenXe.setLaiXe(taiXe);

        rest.put("https://transportnhom14server.herokuapp.com/api/chuyenxe", chuyenXe);

        return "redirect:/chuyenxe";
    }

    @GetMapping("/search")
    public String getSearch(@RequestParam("search__type") String search__type,
                            @RequestParam("keyword") String keyword, Model model) {

        if (search__type.contains("id")) {
            try {
                Integer.parseInt(keyword);
            } catch (Exception e) {
                model.addAttribute("error", "Nhập là 1 số");
                return "forward:/chuyenxe";
            }
        }
        if (keyword.equals("") || search__type.equals("")) {
            return "forward:/chuyenxe";
        }

        List<ChuyenXe> list = Arrays.asList(rest.getForObject("https://transportnhom14server.herokuapp.com/api/chuyenxe/search/{search__type}/{keyword}",
                ChuyenXe[].class,
                search__type,
                keyword));

        if (list == null) {
            return "forward:/chuyenxe";
        }

        model.addAttribute("listChuyenXe", list);
        model.addAttribute("keyword", keyword);
        chuyenXeDefault = new ArrayList<>(list);
        listChuyenXe=new ArrayList<>(list);

        return "chuyenxe/listChuyenXe";
    }

    @GetMapping("/sort")
    public String sort(Model model, @RequestParam String sort__type, HttpServletRequest request) {

        if (sort__type.equals("tenLaiXe")) {
            listChuyenXe.sort(Comparator.comparing((ChuyenXe chuyenXe) -> chuyenXe.getLaiXe().getTen()));
        }
        if (sort__type.equals("cmtLaiXe")) {
            listChuyenXe.sort(Comparator.comparing((ChuyenXe chuyenXe) -> chuyenXe.getLaiXe().getCMT()));
        }
        if (sort__type.equals("tenPhuXe")) {
            listChuyenXe.sort(Comparator.comparing((ChuyenXe chuyenXe) -> chuyenXe.getPhuXe().getTen()));
        }
        if (sort__type.equals("cmtPhuXe")) {
            listChuyenXe.sort(Comparator.comparing((ChuyenXe chuyenXe) -> chuyenXe.getPhuXe().getCMT()));
        }
        if (sort__type.equals("diemDau")) {
            listChuyenXe.sort(Comparator.comparing((ChuyenXe chuyenXe) -> chuyenXe.getTuyenXe().getDiemDau()));
        }
        if (sort__type.equals("diemCuoi")) {
            listChuyenXe.sort(Comparator.comparing((ChuyenXe chuyenXe) -> chuyenXe.getTuyenXe().getDiemCuoi()));
        }
        if (sort__type.equals("ngayKhoiHanh")) {
            listChuyenXe.sort(Comparator.comparing((ChuyenXe chuyenXe) -> chuyenXe.getNgayKhoiHanh()));
        }
        if (sort__type.equals("bienSoXe")) {
            listChuyenXe.sort(Comparator.comparing((ChuyenXe chuyenXe) -> chuyenXe.getXeKhach().getBienSo()));
        }
        if (sort__type.equals("giaVe")) {
            listChuyenXe.sort(Comparator.comparing((ChuyenXe chuyenXe) -> chuyenXe.getGiaVe()));
        }
        if (sort__type.equals("soKhach")) {
            listChuyenXe.sort(Comparator.comparing((ChuyenXe chuyenXe) -> chuyenXe.getSoKhach()));
        }
        if (sort__type.equals("Default")) {
            listChuyenXe.sort(Comparator.comparing((ChuyenXe chuyenXe) -> chuyenXe.getId()));
        }

        model.addAttribute("listChuyenXe", listChuyenXe);

        return "chuyenxe/listChuyenXe";
    }


}
