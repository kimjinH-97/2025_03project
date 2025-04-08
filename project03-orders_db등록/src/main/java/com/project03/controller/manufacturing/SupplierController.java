package com.project03.controller.manufacturing;

import com.project03.domain.Supplier;
import com.project03.repository.manufacturing.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/suppliers")
public class SupplierController {

    private final SupplierRepository supplierRepository;

    @GetMapping("/list")
    public String showSuppliersList(Model model) {
        List<Supplier> suppliers = supplierRepository.findAll();
        model.addAttribute("suppliers", suppliers);
        return "supplier/list";
    }


    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("supplier", new Supplier());
        return "supplier/register";
    }

    @PostMapping("/register")
    public String registerSupplier(@ModelAttribute Supplier supplier) {
        supplierRepository.save(supplier);
        return "redirect:/suppliers/list"; // 등록 후 리스트 페이지로 이동
    }


}
