package com.project03.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = "processstep") // 여기가 핵심!
public class ProcessStep {
    @Id
    private Long sequence;

    private String description;

    private String deptId;

    @OneToMany(mappedBy = "processStep")
    private List<Product> products;

    // Getter 추가!
    public Long getSequence() {
        return sequence;
    }

}
