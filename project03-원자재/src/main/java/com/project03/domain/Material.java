package com.project03.domain;


import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "imageSet")
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long materialId;
    @Column(length = 255, nullable = false)
    private String materialName;
    @Column(length = 255, nullable = false)
    private String materialQuantity;
    @Column(length = 255)
    private String materialSize;
    @Column(length = 255)
    private String materialDescription;

    public void change(String materialName, String materialQuantity,String materialSize, String materialDescription){
        this.materialName = materialName;
        this.materialQuantity = materialQuantity;
        this.materialSize = materialSize;
        this.materialDescription = materialDescription;
    }

    @OneToMany(mappedBy = "material", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<MaterialImage> imageSet = new HashSet<>();

    public void addImage(String uuid, String fileName){
        MaterialImage materialImage = MaterialImage.builder()
                .MaterialUuid(uuid)
                .MaterialFileName(fileName)
                .material(this)
                .MaterialOrd(imageSet.size())
                .build();
        imageSet.add(materialImage);
    }

    public void clearImages(){
        imageSet.forEach(materialImage -> materialImage.changeMaterial(null));
        this.imageSet.clear();
    }
}
