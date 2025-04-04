package com.project03.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Material {

    @Id
    private Long materialId;

    private String materialDescription;

    private String materialName;

    private Long materialQuantity;

    private Long materialSize;

    // Getter & Setter
    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public String getMaterialDescription() {
        return materialDescription;
    }

    public void setMaterialDescription(String materialDescription) {
        this.materialDescription = materialDescription;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public Long getMaterialQuantity() {
        return materialQuantity;
    }

    public void setMaterialQuantity(Long materialQuantity) {
        this.materialQuantity = materialQuantity;
    }

    public Long getMaterialSize() {
        return materialSize;
    }

    public void setMaterialSize(Long materialSize) {
        this.materialSize = materialSize;
    }
}

