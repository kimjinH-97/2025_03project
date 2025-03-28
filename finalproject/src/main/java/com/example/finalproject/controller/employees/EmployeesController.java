package com.example.finalproject.controller.employees;

import com.example.finalproject.domain.login.Employees;
import com.example.finalproject.dto.employees.EmployeesDTO;
import com.example.finalproject.dto.employees.EmployeesPageRequestDTO;
import com.example.finalproject.dto.employees.EmployeesPageResponseDTO;
import com.example.finalproject.service.employees.EmployeesService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;


@Controller
@RequiredArgsConstructor
@Log4j2
@SessionAttributes("employee")
public class EmployeesController {
    private final EmployeesService employeesService;

    @GetMapping("/employeesList")
    public String employeesList(EmployeesPageRequestDTO employeesPageRequestDTO, Model model) {

        EmployeesPageResponseDTO<EmployeesDTO> responseDTO = employeesService.list(employeesPageRequestDTO);
        log.info(responseDTO);
        model.addAttribute("responseDTO", responseDTO);
        // 'employee' 객체는 세션에서 자동으로 모델에 바인딩됨
        return "employeesList"; // mainTest.html 템플릿을 반환
    }


//    @GetMapping("/employeesList")
//    public String employeesList(HttpSession session, Model model){
//        Employees employee = (Employees) session.getAttribute("employee");
//        if (employee != null) {
//            model.addAttribute("employee", employee);
//        }
//        return "employeesList"; // mainTest.html 템플릿을 반환
//    }


//    @GetMapping("/employeesList")
//    public String employeesList(@SessionAttribute(name = "employee", required = false) Employees employee, Model model) {
//        if (employee != null) {
//            model.addAttribute("employee", employee);
//        } else {
//            model.addAttribute("error", "로그인이 필요합니다.");
//        }
//        return "employeesList"; // mainTest.html 템플릿을 반환
//    }


}
