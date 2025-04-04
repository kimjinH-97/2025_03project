package com.project03.controller;

import com.project03.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * 제품 공정 단계 이동 (다음 단계로)
     */
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
}
