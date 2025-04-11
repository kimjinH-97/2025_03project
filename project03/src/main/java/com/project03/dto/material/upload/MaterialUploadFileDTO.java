package com.project03.dto.material.upload;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class MaterialUploadFileDTO {
    private List<MultipartFile> files;
}
