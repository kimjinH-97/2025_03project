package com.example.finalproject.controller.login;

import com.example.finalproject.domain.login.Employees;
import com.example.finalproject.dto.login.ApiResponse;
import com.example.finalproject.dto.login.LoginDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.finalproject.service.login.LoginService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Log4j2
@Controller
public class LoginController {

    private final LoginService loginService;

    //생성자 주입을 위해 Autowired 어노테이션 사용
    // (스프링이 employeeService 를 자동으로 주입하도록)
    @Autowired
    public LoginController(LoginService loginService){
        this.loginService = loginService;
    }

    // 로그인 페이지로 이동
    @GetMapping("/login")
    public void login(){
    }


    // 로그인 인증처리
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO, HttpSession session) {
        log.info("로그인 요청 받음: {} {}", loginDTO.getUserId(), loginDTO.getPassword());

        // 로그인 처리
        Employees employee = loginService.login(loginDTO, session);

        // 로그인 실패 시
        if (employee == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)  // 401 Unauthorized 상태 코드
                    .body(new ApiResponse(false, "아이디 또는 비밀번호가 일치하지 않습니다."));
        }

        // 로그인 성공 시 세션에 사용자 정보 저장
        session.setAttribute("employee", employee);
        session.setMaxInactiveInterval(3600);  // 세션 유지시간 1시간 설정

        // 로그인 성공 응답
        return ResponseEntity
                .ok(new ApiResponse(true, "로그인 성공"));
    }
}
