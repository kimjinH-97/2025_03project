package com.project03.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Material {

    @Id
    private Long materialId;

    private String materialDescription;

    private String materialName;

    private Long materialQuantity;

    private Long materialSize;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

}

