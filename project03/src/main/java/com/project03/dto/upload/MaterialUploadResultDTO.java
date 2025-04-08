package com.project03.dto.upload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MaterialUploadResultDTO {
    private String MaterialUuid;
    private String MaterialFileName;
    private boolean MaterialImg;
    public String getLink(){
        if (MaterialImg){
            return "s_" + MaterialUuid + "_" + MaterialFileName;    //이미지 경우 섬네일
        } else {
            return MaterialUuid + "_" + MaterialFileName;
        }
    }
}
