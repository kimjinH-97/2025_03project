package com.project03.service.manufacturing;

import com.project03.domain.Material;
import com.project03.dto.material.MaterialDTO;
import com.project03.dto.material.MaterialPageRequestDTO;
import com.project03.dto.material.MaterialPageResponseDTO;
import com.project03.repository.manufacturing.MaterialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class MaterialServiceImpl implements MaterialServices{
    private final ModelMapper modelMapper;
    private final MaterialRepository materialRepository;

    @Override
    public Long register(MaterialDTO materialDTO){
        Material material = dtoToEntity(materialDTO);

        Long materialId = materialRepository.save(material).getMaterialId();

        return materialId;
    }

    @Override
    public MaterialDTO readOne(Long materialId){
        Optional<Material> result = materialRepository.findByIdWithImages(materialId);
        Material material = result.orElseThrow();
        MaterialDTO materialDTO = entityToDTO(material);
        return materialDTO;
    }

    @Transactional
    @Override
    public void modify(MaterialDTO materialDTO){
        Optional<Material> result = materialRepository.findById(materialDTO.getMaterialId());
        Material material = result.orElseThrow();
        material.change(materialDTO.getMaterialName(), materialDTO.getMaterialQuantity(), materialDTO.getMaterialSize(), materialDTO.getMaterialDescription());

        //첨부파일의 처리
        material.clearImages();

        if (materialDTO.getMaterialFileNames() != null){
            for (String fileName : materialDTO.getMaterialFileNames()){
                String[] arr = fileName.split("_");
                material.addImage(arr[0], arr[1]);
            }
        }

        materialRepository.save(material);
    }

    @Override
    @Transactional
    public void remove(Long materialId){
        materialRepository.deleteById(materialId);
    }

    @Override
    public MaterialPageResponseDTO<MaterialDTO> listwithAll(MaterialPageRequestDTO materialPageRequestDTO){
        String[] types = materialPageRequestDTO.getTypes();
        String keyword = materialPageRequestDTO.getKeyword();
        Pageable pageable = materialPageRequestDTO.getPageable("materialId");

        Page<Material> result = materialRepository.searchAll(types, keyword, pageable);

        List<MaterialDTO> dtoList = result.getContent().stream().map(material -> modelMapper.map(material, MaterialDTO.class)).collect(Collectors.toList());

        return MaterialPageResponseDTO.<MaterialDTO>withAll()
                .materialPageRequestDTO(materialPageRequestDTO)
                .dtoList(dtoList)
                .total((int)result.getTotalElements())
                .build();
    }
}
