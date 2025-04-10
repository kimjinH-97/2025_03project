package com.project03.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private String productName;

    private Date manufactureDate;

    private String description;

    private Long quantity;

    private String materialDescription; // 소형 / 중형 / 대형


    @ManyToOne
    @JoinColumn(name = "sequence")
    private ProcessStep processStep;


}

