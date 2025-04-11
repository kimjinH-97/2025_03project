package com.project03.controller.manufacturing.advice;

import com.project03.dto.material.upload.MaterialUploadFileDTO;
import com.project03.dto.material.upload.MaterialUploadResultDTO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@Log4j2
public class MaterialUpDownController {
    @Value("${com.project03.upload.path}")
    private String uploadPath;

    @ApiOperation(value = "Upload POST", notes = "POST 방식으로 파일 등록")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<MaterialUploadResultDTO> upload(MaterialUploadFileDTO materialUploadFileDTO){
        log.info(materialUploadFileDTO);

        if (materialUploadFileDTO.getFiles() != null){
            List<MaterialUploadResultDTO> list = new ArrayList<>();

            materialUploadFileDTO.getFiles().forEach(multipartFile -> {
                String originalName = multipartFile.getOriginalFilename();
                log.info(originalName);

                //첨부파일 이름 규칙 -> UUID_기존의첨부파일이름
                String uuid = UUID.randomUUID().toString(); //임의의 문자열
                String newFileName = uuid + "_" + originalName;

                Path savaPath = Paths.get(uploadPath, newFileName);

                boolean image = false;

                try{
                    multipartFile.transferTo(savaPath);

                    //이미지 파일의 종류라면
                    if (Files.probeContentType(savaPath).startsWith("image")){
                        image = true;
                        File thumbFile = new File(uploadPath, "s_" + uuid + "_" + originalName);

                        Thumbnailator.createThumbnail(savaPath.toFile(), thumbFile, 200, 200);
                    }
                } catch (IOException e){
                    e.printStackTrace();
                }

                MaterialUploadResultDTO materialUploadResultDTO = MaterialUploadResultDTO.builder()
                        .MaterialUuid(uuid)
                        .MaterialFileName(originalName)
                        .MaterialImg(image)
                        .build();
                list.add(materialUploadResultDTO);
            });
            return list;
        }

        return null;
    }

    //첨부파일 조회 api
    @ApiOperation(value = "view 파일", notes = "GET 방식으로 첨부파일 조회")
    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFileGET(@PathVariable String fileName){
        Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);
        String resourceName = resource.getFilename();
        log.info("resourceName: " + resourceName);

        HttpHeaders headers = new HttpHeaders();
        try {
            headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
        }catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().headers(headers).body(resource);
    }

    //첨부파일 삭제 api
    @ApiOperation(value = "remove 파일", notes = "DELETE 방식으로 파일 삭제")
    @DeleteMapping("/remove/{fileName}")
    public Map<String,Boolean> removeFile(@PathVariable String fileName){
        Resource resource = new FileSystemResource(uploadPath+File.separator+fileName);
        String resourceName = resource.getFilename();

        Map<String, Boolean> resultMap = new HashMap<>();
        boolean removed = false;
        try {
            String contentType = Files.probeContentType(resource.getFile().toPath());
            removed = resource.getFile().delete();

            if (contentType.startsWith("image")){
                File thumbnailFile = new File(uploadPath+File.separator + "s_" + fileName);
                thumbnailFile.delete();
            }
        } catch (Exception e){
            log.error(e.getMessage());
        }

        resultMap.put("result", removed);
        return resultMap;
    }

}
