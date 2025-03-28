package com.example.finalproject.dto.employees;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class EmployeesDTO {
    @NotEmpty
    private String userId;

    private String password;

    private String userName;

    private String section;

    private String email;

    private String phoneNumber;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate regDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate modDate;

    private String department;











}
