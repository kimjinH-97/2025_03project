package com.project03.controller.manufacturing;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProcessController {
    @GetMapping("/process")
    public String processPage(){ return  "process"; }
}
