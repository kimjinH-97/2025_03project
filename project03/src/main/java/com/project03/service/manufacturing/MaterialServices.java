package com.project03.service.manufacturing;

import com.project03.domain.Material;
import com.project03.dto.material.MaterialDTO;
import com.project03.dto.material.MaterialPageRequestDTO;
import com.project03.dto.material.MaterialPageResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

public interface MaterialServices {
    Long register(MaterialDTO materialDTO);

    MaterialDTO readOne(Long materialId);

    void modify(MaterialDTO materialDTO);

    void remove(Long materialId);

    MaterialPageResponseDTO<MaterialDTO> listwithAll(MaterialPageRequestDTO materialPageRequestDTO);

    default Material dtoToEntity(MaterialDTO materialDTO){
        Material material = Material.builder()
                .materialName(materialDTO.getMaterialName())
                .materialQuantity(materialDTO.getMaterialQuantity())
                .materialSize(materialDTO.getMaterialSize())
                .materialDescription(materialDTO.getMaterialDescription())
                .build();

        if (materialDTO.getMaterialFileNames() != null){
            materialDTO.getMaterialFileNames().forEach(fileName -> {
                String[] arr = fileName.substring(8).split("_");
                material.addImage(arr[0], arr[1]);
            });
        }
        return material;
    }

    default MaterialDTO entityToDTO(Material material){
        MaterialDTO materialDTO = MaterialDTO.builder()
                .materialId(material.getMaterialId())
                .materialName(material.getMaterialName())
                .materialQuantity(material.getMaterialQuantity())
                .materialSize(material.getMaterialSize())
                .materialDescription(material.getMaterialDescription())
                .build();

        List<String> fileNames =
                material.getImageSet().stream().sorted().map(materialImage ->
                        materialImage.getMaterialUuid()+"_"+materialImage.getMaterialFileName()).collect(Collectors.toList());

        materialDTO.setMaterialFileNames(fileNames);

        return materialDTO;
    }
}
