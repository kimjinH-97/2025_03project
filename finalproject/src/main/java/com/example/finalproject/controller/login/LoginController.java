package com.example.finalproject.controller.login;

import com.example.finalproject.domain.login.Employees;
import com.example.finalproject.dto.login.LoginDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.finalproject.service.login.EmployeesService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


@Controller
public class LoginController {

    private final EmployeesService employeesService;

    //생성자 주입을 위해 Autowired 어노테이션 사용
    // (스프링이 employeeService 를 자동으로 주입하도록)
    @Autowired
    public LoginController(EmployeesService employeesService){
        this.employeesService = employeesService;
    }

    // 로그인 페이지로 이동
    @GetMapping("/login")
    public void login(){

    }

    // 로그인 인증처리
    @PostMapping("/login")
    public String login(LoginDTO loginDTO, HttpSession session, Model model){
        Employees employee = employeesService.login(loginDTO, session);

        // 로그인 실패 시(null값을 리턴하라고 serviceImpl에 설정 해 놓음)
        if(employee == null){
            model.addAttribute("errorMessage", "아이디 또는 비밀번호가 일치하지 않습니다.");
            return "login";
        }
        return "redirect:/home";
    }
}
