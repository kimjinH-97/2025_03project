package com.project03;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class Project03Application {

	public static void main(String[] args) {
		String rawPassword = "123";
		String encodedPassword = new BCryptPasswordEncoder().encode(rawPassword);
		System.out.println(encodedPassword);
		SpringApplication.run(Project03Application.class, args);
	}

}
