package com.project03.controller.manufacturing;


import com.project03.domain.Product;
import com.project03.repository.manufacturing.ProductRepository;
import com.project03.service.manufacturing.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/process")
public class ProcessController {

    private final ProductService productService;
    private ProductRepository productRepository;

    public ProcessController(ProductService productService, ProductRepository productRepository) {
        this.productService = productService;
        this.productRepository = productRepository;
    }

    @GetMapping
    public String processPage() {
        return "process";
    }

    // 공정 취소 기능
    @PostMapping("/cancel/{productId}")
    @ResponseBody
    public String cancelProductToMaterial(@PathVariable Long productId) {
        boolean success = productService.cancelProductToMaterial(productId);
        if (success) {
            return "제품이 원자재로 되돌아갔습니다.";
        } else {
            return "0단계 공정이 아니거나 잘못된 ID입니다.";
        }
    }
    //시퀀스가 0인걸 비동기식으로 보여주는 컨트롤러
    @GetMapping("/available-products")
    @ResponseBody
    public List<Map<String, Object>> getAvailableProducts() {
        List<Product> products = productRepository.findByProcessStep_Sequence(0L);
        List<Map<String, Object>> response = new ArrayList<>();

        for (Product product : products) {
            Map<String, Object> map = new HashMap<>();
            map.put("productId", product.getProductId()); // 정확하게 productId
            map.put("productName", product.getProductName());
            response.add(map);
        }

        return response;
    }



}

