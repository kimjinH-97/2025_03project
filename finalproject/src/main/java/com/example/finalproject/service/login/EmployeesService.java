package com.example.finalproject.service.login;

import com.example.finalproject.domain.login.Employees;
import com.example.finalproject.dto.login.LoginDTO;
import com.example.finalproject.dto.login.RegisterDTO;
import jakarta.servlet.http.HttpSession;

import java.net.http.HttpHeaders;

public interface EmployeesService {

    Employees login(LoginDTO loginDTO, HttpSession session);

    Employees registerEmployee(RegisterDTO registerDTO);
}
