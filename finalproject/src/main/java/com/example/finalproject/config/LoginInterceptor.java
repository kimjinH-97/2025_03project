package com.example.finalproject.config;

import com.example.finalproject.domain.login.Employees;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Log4j2
public class LoginInterceptor implements HandlerInterceptor {

    /* 로그인 상태를 체크하고 세션 관리를 담당
    인증되지 않은 사용자를 특정 페이지(초기화면(login))으로 리디렉션 하는 용도로 사용.
     */

    // 로그아웃 후 뒤로가기 방지
    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) throws Exception{
        response.setHeader("Cache-control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
        response.setHeader("Expires", "0"); // Proxies.

        /**
         *         Cache-Control: 캐시를 저장하지 않도록 설정.
         *         Pragma: HTTP 1.0에서 캐시를 비활성화하는 헤더.
         *         Expires: 캐시 만료 시간을 0으로 설정하여 캐시가 없도록 함.
         */
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 로그인 페이지 요청은 예외 처리
        String requestURI = request.getRequestURI();
        if (requestURI.contains("/register")) {
            return true; // register 페이지는 로그인 상태와 관계없이 접근 허용
        }

        // 현재 요청에 대한 세션을 가져옴.
        HttpSession session = request.getSession();
        // 세션에 저장된 employees 객체를 가져옴.(=로그인한 사용자의 정보)
        Employees employee = (Employees) session.getAttribute("employee");
        if (employee != null) {
            log.info("로그인 됨, 요청을 계속 처리");
            return true;
        }

        // 로그인 되지 않았을 시 로그인 페이지로 리디렉션
        log.info("로그인되지 않음, 로그인 페이지로 리디렉션");
        session.setAttribute("alertMessage", "모든 페이지는 로그인 후 이용 가능합니다.");
        response.sendRedirect("/login");
        return false; // 요청 처리 중단
    }
}