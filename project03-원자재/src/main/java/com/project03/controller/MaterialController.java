package com.project03.controller;

import com.project03.dto.MaterialDTO;
import com.project03.dto.MaterialPageRequestDTO;
import com.project03.dto.MaterialPageResponseDTO;
import com.project03.service.MaterialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

@Controller
@RequestMapping("/material")
@Log4j2
@RequiredArgsConstructor
public class MaterialController {
    private final MaterialService materialService;

    @Value("${com.project03.upload.path}")
    private String uploadPath;

    @GetMapping("/MaterialList")
    public void materialList(MaterialPageRequestDTO materialPageRequestDTO, Model model){
        MaterialPageResponseDTO<MaterialDTO> responseDTO = materialService.listwithAll(materialPageRequestDTO);
        model.addAttribute("responseDTO", responseDTO);
    }

    @GetMapping("/MaterialRegister")
    public void registerGET(){

    }

    @PostMapping("/MaterialRegister")
    public String registerPost(@Valid MaterialDTO materialDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes){
        if (bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/material/MaterialRegister";
        }

        Long materialId = materialService.register(materialDTO);
        redirectAttributes.addFlashAttribute("result", materialId);
        return "redirect:/material/MaterialList";
    }

    @GetMapping("/MaterialModify")
    public void read(Long materialId, MaterialPageRequestDTO materialPageRequestDTO, Model model){
        MaterialDTO materialDTO = materialService.readOne(materialId);
        model.addAttribute("dto", materialDTO);
    }

    @PostMapping("/MaterialModify")
    public String modify(MaterialPageRequestDTO materialPageRequestDTO,
                         @Valid MaterialDTO materialDTO,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes){
        if (bindingResult.hasErrors()){
            String link = materialPageRequestDTO.getLink();
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            redirectAttributes.addAttribute("materialId", materialDTO.getMaterialId());
            return "redirect:/material/MaterialModify?"+link;
        }
        materialService.modify(materialDTO);
        redirectAttributes.addFlashAttribute("result", "modified");
        redirectAttributes.addAttribute("materialId", materialDTO.getMaterialId());
        return "redirect:/material/MaterialList";
    }

    @PostMapping("/MaterialRemove")
    public String remove(MaterialDTO materialDTO, RedirectAttributes redirectAttributes){
        Long materialId = materialDTO.getMaterialId();

        materialService.remove(materialId);

        List<String> fileNames = materialDTO.getMaterialFileNames();
        if (fileNames != null && fileNames.size() > 0){
            removeFiles(fileNames);
        }

        redirectAttributes.addFlashAttribute("result", "removed");
        return "redirect:/material/MaterialList";
    }

    public void removeFiles(List<String> files){
        for (String fileName:files){
            Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);
            String resourceName = resource.getFilename();

            try {
                //원본 파일 삭제
                String contentType = Files.probeContentType(resource.getFile().toPath());
                resource.getFile().delete();

                if (contentType.startsWith("image")){
                    File thumbnailFile = new File(uploadPath + File.separator + "s_" + fileName);
                    thumbnailFile.delete();
                }
            }catch (Exception e){
                log.error(e.getMessage());
            }
        }
    }
}
