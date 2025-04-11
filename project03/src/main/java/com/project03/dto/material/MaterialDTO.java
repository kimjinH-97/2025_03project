package com.project03.dto.material;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MaterialDTO {
    private Long materialId;
    @NotEmpty
    private String materialName;
    @NotEmpty
    private String materialQuantity;
    private String materialSize;
    private String materialDescription;

    //첨부파일 이름들
    private List<String> materialFileNames;
}
