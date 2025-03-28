package com.example.finalproject.service.login;

import com.example.finalproject.domain.login.Employees;
import com.example.finalproject.dto.login.LoginDTO;
import com.example.finalproject.dto.login.RegisterDTO;
import com.example.finalproject.repository.login.EmployeesRepository;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Log4j2
@Service
public class LoginServiceImpl implements LoginService {
    private final EmployeesRepository employeesRepository;
    private final PasswordEncoder passwordEncoder;

    // 생성자 주입 방식
    @Autowired
    public LoginServiceImpl(EmployeesRepository employeesRepository, PasswordEncoder passwordEncoder) {
        this.employeesRepository = employeesRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 로그인 인증 기능 구현용
    @Override
    public Employees login(LoginDTO loginDTO, HttpSession session){
        // 사용자가 입력한 username 기준으로 db에서 사용자를 조회
        Optional<Employees> optionalEmployees = employeesRepository.findByUserId(loginDTO.getUserId());

        //만약 존재한다면
        if (optionalEmployees.isEmpty()) {
            return null;
        }
        Employees employee = optionalEmployees.get();

        if (passwordEncoder.matches(loginDTO.getPassword(), employee.getPassword())) {
            log.info("로그인 성공: 비밀번호 일치 - 입력한 비밀번호: {}, DB 저장된 비밀번호: {}", loginDTO.getPassword(), employee.getPassword());
            session.setAttribute("employee", employee);
            return employee;
        }

        return null;
    }


    // 회원가입 메서드
    @Override
    public Employees registerEmployee(RegisterDTO registerDTO){
        // 이미 존재하는 사용자 이름 체크
        Optional<Employees> existingEmployee = employeesRepository.findByUserId(registerDTO.getUserId());
        if (existingEmployee.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자 이름입니다.");
        }

        // 새 직원 객체 생성
        Employees employee = new Employees();
        employee.setUserId(registerDTO.getUserId());
        employee.setPassword(passwordEncoder.encode(registerDTO.getPassword())); // 비밀번호 암호화
        employee.setUserName(registerDTO.getUserName());
        employee.setDepartment(registerDTO.getSection());
        employee.setDepartment(registerDTO.getDepartment());
        employee.setEmail(registerDTO.getEmail());
        employee.setPhoneNumber(registerDTO.getPhoneNumber());

        return employeesRepository.save(employee);
    }
}
