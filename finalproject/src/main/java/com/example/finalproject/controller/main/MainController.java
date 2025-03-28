package com.example.finalproject.controller.main;

import com.example.finalproject.domain.login.Employees;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes("employee")
public class MainController {

    @GetMapping("/main")
    public String main(Model model) {

        return "main"; // mainTest.html 템플릿을 반환
    }
}

