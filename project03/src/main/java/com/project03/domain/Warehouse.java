package com.project03.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;
    private String description;
    private Long quantity;
    private String materialDescription;
    private Date movedDate; // 입고 날짜
    @Column(nullable = false)
    private boolean isOrdered = false;

}

