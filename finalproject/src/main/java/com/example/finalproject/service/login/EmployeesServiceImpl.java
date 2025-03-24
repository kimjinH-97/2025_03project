package com.example.finalproject.service.login;

import com.example.finalproject.domain.login.Employees;
import com.example.finalproject.dto.login.LoginDTO;
import com.example.finalproject.dto.login.RegisterDTO;
import com.example.finalproject.repository.login.EmployeesRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

@Service
public class EmployeesServiceImpl implements EmployeesService {
    private final EmployeesRepository employeesRepository;
    private final PasswordEncoder passwordEncoder;

    // 생성자 주입 방식
    @Autowired
    public EmployeesServiceImpl(EmployeesRepository employeesRepository, PasswordEncoder passwordEncoder) {
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

        // 비밀번호가 맞지 않으면 null 반환
        if (!passwordEncoder.matches(loginDTO.getPassword(), employee.getPassword())) {
            return null;
        }
        // 세션에 사용자 정보 저장
        session.setAttribute("employee", employee);
        return employee;
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
        employee.setName(registerDTO.getName());
        employee.setDepartment(registerDTO.getDepartment());
        employee.setEmail(registerDTO.getEmail());
        employee.setPhoneNumber(registerDTO.getPhoneNumber());

        return employeesRepository.save(employee);
    }
}
