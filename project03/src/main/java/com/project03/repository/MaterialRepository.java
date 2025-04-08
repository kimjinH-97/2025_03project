package com.project03.repository;

import com.project03.domain.Material;
import com.project03.repository.search.MaterialSearch;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface MaterialRepository extends JpaRepository<Material, Long>, MaterialSearch {

    Page<Material> searchAll(String[] types, String keyword, Pageable pageable);

    @EntityGraph(attributePaths = {"imageSet"})
    @Query("select b from Material b where b.materialId = :materialId")
    Optional<Material> findByIdWithImages(Long materialId);


}
