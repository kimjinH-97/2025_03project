package com.project03.service;

import com.project03.dto.MaterialDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.UUID;

@SpringBootTest
public class MaterialServiceTests {
    @Autowired
    private MaterialService materialService;

    @Test
    public void testRegister(){
        MaterialDTO materialDTO = MaterialDTO.builder()
                .materialName("PET필름")
                .materialQuantity("100")
                .materialSize("500")
                .materialDescription("중형")
                .build();
        Long materialId = materialService.register(materialDTO);
    }

    @Test
    public void testModify(){
        MaterialDTO materialDTO = MaterialDTO.builder()
                .materialId(1L)
                .materialName("1")
                .materialQuantity("1")
                .materialSize("1")
                .materialDescription("1")
                .build();
        materialService.modify(materialDTO);
    }

    @Test
    public void testRegisterWithImages(){
        MaterialDTO materialDTO = MaterialDTO.builder()
                .materialName("1")
                .materialQuantity("1")
                .materialSize("1")
                .materialDescription("1")
                .build();

        materialDTO.setMaterialFileNames(Arrays.asList(
                UUID.randomUUID()+"_aaa.jpg",
                UUID.randomUUID()+"_bbb.jpg",
                UUID.randomUUID()+"_ccc.jpg"
        ));

        Long materialId = materialService.register(materialDTO);
    }

    @Test
    public void testImageModify(){
        MaterialDTO materialDTO = MaterialDTO.builder()
                .materialId(17L)
                .materialName("2")
                .materialQuantity("2")
                .materialSize("2")
                .materialDescription("2")
                .build();
        materialDTO.setMaterialFileNames(Arrays.asList(UUID.randomUUID()+"_zzz.jpg"));

        materialService.modify(materialDTO);
    }

    @Test
    public void testImageRemoveAll(){
        Long materialId = 17L;
        materialService.remove(materialId);
    }
}
