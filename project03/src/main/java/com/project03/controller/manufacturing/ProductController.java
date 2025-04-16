package com.project03.controller.manufacturing;

import com.project03.domain.Material;
import com.project03.dto.material.MaterialPageRequestDTO;
import com.project03.dto.material.MaterialPageResponseDTO;
import com.project03.repository.manufacturing.MaterialRepository;
import com.project03.service.manufacturing.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final MaterialRepository materialRepository;

    public ProductController(ProductService productService, MaterialRepository materialRepository) {
        this.productService = productService;
        this.materialRepository = materialRepository;
    }

    // 제품 공정 단계 이동 (다음 단계로)
    @PostMapping("/{id}/next-step")
    public ResponseEntity<String> moveToNextStep(@PathVariable("id") Long productId) {
        productService.moveToNextStep(productId);
        return ResponseEntity.ok("공정 단계가 다음 단계로 이동했습니다.");
    }

    //  새로운: 원자재를 제품으로 등록
    @PostMapping("/register-from-material/{materialId}")
    public ResponseEntity<String> registerFromMaterial(@PathVariable Long materialId) {
        productService.registerProductFromMaterial(materialId);
        return ResponseEntity.ok("제품으로 등록되었습니다!");
    }

    @GetMapping("/register")
    public String showRegisterForm(MaterialPageRequestDTO requestDTO, Model model) {
        MaterialPageResponseDTO<Material> responseDTO = productService.getPagedMaterials(requestDTO);
        model.addAttribute("result", responseDTO);
        model.addAttribute("requestDTO", requestDTO);
        return "register";
    }




}
