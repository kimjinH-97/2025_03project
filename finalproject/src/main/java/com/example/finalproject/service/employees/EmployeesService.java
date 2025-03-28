package com.example.finalproject.service.employees;

import com.example.finalproject.dto.employees.EmployeesDTO;
import com.example.finalproject.dto.employees.EmployeesPageRequestDTO;
import com.example.finalproject.dto.employees.EmployeesPageResponseDTO;

public interface EmployeesService {
    EmployeesPageResponseDTO<EmployeesDTO> list(EmployeesPageRequestDTO employeesPageRequestDTO);
}
