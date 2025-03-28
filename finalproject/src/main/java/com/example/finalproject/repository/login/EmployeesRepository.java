package com.example.finalproject.repository.login;

import com.example.finalproject.domain.login.Employees;
import com.example.finalproject.repository.search.EmployeesSearch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeesRepository extends JpaRepository<Employees, String>, EmployeesSearch {
    Optional<Employees> findByUserId(String userId); // 사용자 이름 기준으로 검색
    Optional<Employees> findByDepartment(String department); // 부서 기준으로 검색
}
