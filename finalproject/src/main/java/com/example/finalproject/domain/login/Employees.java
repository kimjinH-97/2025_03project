package com.example.finalproject.domain.login;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
public class Employees {
    @Id
    private String userId;
    private String password;
    private String userName;
    private String section;
    private String department;
    private String email;
    private String phoneNumber;

    @Column(updatable = false)
    private LocalDate regDate;
    private LocalDate modDate;
}


