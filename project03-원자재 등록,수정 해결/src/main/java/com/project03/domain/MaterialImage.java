package com.project03.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "material")
public class MaterialImage implements Comparable<MaterialImage> {
    @Id
    private String MaterialUuid;

    private String MaterialFileName;

    private int MaterialOrd;

    @ManyToOne
    private Material material;

    @Override
    public int compareTo(MaterialImage other){
        return this.MaterialOrd - other.MaterialOrd;
    }

    public void changeMaterial(Material material){
        this.material = material;
    }
}
