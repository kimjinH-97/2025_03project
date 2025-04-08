package com.project03.repository;

import com.project03.domain.MaterialImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MaterialImageRepository extends JpaRepository<MaterialImage, Long> {
    @Query("select r from MaterialImage r where r.material.materialId = :materialId")
    Page<MaterialImage> listOfMaterial(@Param("materialId") Long materialId, Pageable pageable);

    void deleteByMaterial_materialId(Long materialId);
}
