package com.project03;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class Project03Application {

	public static void main(String[] args) {
		String rawPassword = "123"; // 원래 비밀번호
		String encodedPassword = new BCryptPasswordEncoder().encode(rawPassword); // 암호화
		System.out.println(encodedPassword); // 결과 출력
		SpringApplication.run(Project03Application.class, args);
	}

}
