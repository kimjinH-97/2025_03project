package com.example.finalproject.controller.login;

import com.example.finalproject.dto.login.RegisterDTO;
import com.example.finalproject.service.login.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@Controller
public class RegisterController {

    private final LoginService loginService;

    @GetMapping("/register")
    public String showRegisterPage(){
        return "register"; // register.html 페이지 이동
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegisterDTO registerDTO, Model model){
        try{
            loginService.registerEmployee(registerDTO);
        }catch(IllegalAccessError e){ // 사용자 이름이 중복될 때
            model.addAttribute("error", e.getMessage());
            return "redirect:/register"; // 이미 존재하는 사용자 이름일 때 오류페이지 이동
        }
        return "redirect:/login?success=true";
    }

}
