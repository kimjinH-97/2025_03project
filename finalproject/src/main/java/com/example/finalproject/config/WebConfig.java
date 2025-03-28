package com.example.finalproject.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {


    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/*") // 모든 url 겨올에 대해 인터셉터 적용
                .excludePathPatterns("/login", "/register"); // 로그인, 회원가입 페이지는 인터셉터에서 제외
    }
}