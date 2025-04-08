package com.project03.domain;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ 이 줄 추가!
    private Long productId;

    private String productName;

    private Date manufactureDate;

    private String description;

    private Long quantity;

    private String materialDescription; // 소형 / 중형 / 대형


    @ManyToOne
    @JoinColumn(name = "sequence")
    private ProcessStep processStep;

    // ✅ Getter
    public Long getQuantity() {
        return quantity;
    }

    public String getProductName() {
        return productName;
    }

    public Date getManufactureDate() {
        return manufactureDate;
    }

    public String getDescription() {
        return description;
    }

    public ProcessStep getProcessStep() {
        return processStep;
    }

    public String getMaterialDescription() {
        return materialDescription;
    }

    public void setMaterialDescription(String materialDescription) {
        this.materialDescription = materialDescription;
    }

    //  Setter
    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setManufactureDate(Date manufactureDate) {
        this.manufactureDate = manufactureDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setProcessStep(ProcessStep processStep) {
        this.processStep = processStep;
    }
}

