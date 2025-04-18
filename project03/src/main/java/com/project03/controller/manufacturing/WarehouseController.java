package com.project03.controller.manufacturing;

import com.project03.domain.Product;
import com.project03.domain.Warehouse;
import com.project03.repository.manufacturing.ProcessStepRepository;
import com.project03.repository.manufacturing.ProductRepository;
import com.project03.repository.manufacturing.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProcessStepRepository processStepRepository;

    @PostMapping("/move")
    public ResponseEntity<String> moveToWarehouse() {
        Long sequence4 = 4L;

        List<Product> products = productRepository.findByProcessStep_Sequence(sequence4);

        for (Product p : products) {
            Warehouse w = new Warehouse();
            w.setProductName(p.getProductName());
            w.setDescription(p.getDescription());
            w.setQuantity(p.getQuantity());
            w.setMaterialDescription(p.getMaterialDescription());
            w.setMovedDate(new Date());

            warehouseRepository.save(w);
            productRepository.delete(p);
        }

        return ResponseEntity.ok(" 시퀀스 4 제품 창고로 이동 완료!");
    }

    @GetMapping("/list")
    public ResponseEntity<List<Warehouse>> getWarehouseList() {
        return ResponseEntity.ok(warehouseRepository.findAll());
    }
}

