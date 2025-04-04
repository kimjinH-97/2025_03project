package com.project03;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderTest {
    public static void main(String[] args) {
        String rawPassword = "123"; // 원래 비밀번호
        String encodedPassword = new BCryptPasswordEncoder().encode(rawPassword); // 암호화
        System.out.println(encodedPassword); // 결과 출력
    }
}
