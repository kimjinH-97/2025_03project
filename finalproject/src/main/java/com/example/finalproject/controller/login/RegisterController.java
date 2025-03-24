package com.example.finalproject.controller.login;

import com.example.finalproject.dto.login.RegisterDTO;
import com.example.finalproject.service.login.EmployeesService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@Controller
public class RegisterController {

    private final EmployeesService employeesService;

    @GetMapping("/register")
    public String showRegisterPage(){
        return "register"; // register.html 페이지 이동
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegisterDTO registerDTO){
        try{
            employeesService.registerEmployee(registerDTO);
        }catch(IllegalAccessError e){
            return "register"; // 이미 존재하는 사용자 이름일 때 오류페이지 이동
        }
        return "redirect:/login";
    }

}
