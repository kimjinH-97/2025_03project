package com.example.finalproject.repository.search;

import com.example.finalproject.domain.login.Employees;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeesSearch {
    Page<Employees> searchAll(String[] types, String keyword, Pageable pageable);
}