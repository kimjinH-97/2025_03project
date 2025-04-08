package com.project03.repository;

import com.project03.domain.Material;
import com.project03.domain.MaterialImage;
import com.project03.service.MaterialService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

@SpringBootTest
public class MaterialRepositoryTests {
    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private MaterialImageRepository materialImageRepository;

    @Test
    public void testInsert(){
        IntStream.rangeClosed(1,10).forEach(i -> {
            Material material = Material.builder()
                    .materialName("test" + i)
                    .materialSize("test" + i)
                    .materialDescription("test" + i)
                    .build();
            Material result =materialRepository.save(material);

        });
    }

    @Test
    public void testSelect(){
        Long materialId = 1L;
        Optional<Material> result = materialRepository.findById(materialId);
        Material material = result.orElseThrow();

    }

//    @Test
//    public void testUpdate(){
//        Long materialId = 1L;
//        Optional<Material> result = materialRepository.findById(materialId);
//        Material material = result.orElseThrow();
//        material.change("1", "1", "1");
//        materialRepository.save(material);
//    }

    @Test
    public void testInsertWithImages(){
        Material material = Material.builder()
                .materialName("1")
                .materialQuantity("1")
                .materialSize("1")
                .materialDescription("1")
                .build();

        for (int i = 0; i < 3; i++){
            material.addImage(UUID.randomUUID().toString(), "file"+i+".jpg");
        }

        materialRepository.save(material);
    }

    @Test
    @Transactional
    @Commit
    public void testRemoveAll(){
        Long materialId = 16L;

        materialImageRepository.deleteByMaterial_materialId(materialId);

        materialRepository.deleteById(materialId);

    }
}
