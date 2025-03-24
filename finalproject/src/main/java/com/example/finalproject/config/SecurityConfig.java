package com.example.finalproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // BCryptPasswordEncoder를 빈으로 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // SecurityFilterChain을 사용하여 보안 설정을 구성
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/login", "/register", "/css/**", "/js/**").permitAll() // 로그인 및 회원가입 페이지, 정적 리소스는 누구나 접근 가능
                        .anyRequest().authenticated() // 그 외의 요청은 인증된 사용자만 접근 가능
                )
                .formLogin(form -> form
                        .loginPage("/login") // 로그인 페이지 URL
                        .loginProcessingUrl("/login") // 로그인 요청을 처리할 URL
                        .permitAll() // 로그인 페이지는 모두 허용
                )
                .logout(logout -> logout
                        .permitAll() // 로그아웃도 모두 허용
                );

        return http.build(); // 보안 필터 체인 반환
    }
}