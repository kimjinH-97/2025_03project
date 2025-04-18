package com.project03.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "processstep")
public class ProcessStep {
    @Id
    private Long sequence;

    private String description;

    private String deptId;

    @OneToMany(mappedBy = "processStep")
    private List<Product> products;

}
