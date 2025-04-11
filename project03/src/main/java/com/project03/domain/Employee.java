package com.project03.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "employees")
public class Employee {

    @Id
    private String userId;

    private String password;
    private String userName;
    private String section;
    private String email;
    private String phoneNumber;
}
