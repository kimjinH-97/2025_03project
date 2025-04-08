package com.project03.controller.manufacturing;

import com.project03.domain.Material;
import com.project03.service.manufacturing.MaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/materials")
@RequiredArgsConstructor
public class MaterialController {
    private final MaterialService materialService;

    @GetMapping
    public List<Material> getAllMaterials() {
        return materialService.getAllMaterials();
    }
}

