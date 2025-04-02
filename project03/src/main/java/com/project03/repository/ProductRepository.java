package com.project03.repository;


import com.project03.domain.ProcessStep;
import com.project03.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // 특정 공정 단계에 있는 제품들 조회
    List<Product> findByProcessStep_Sequence(Long sequence);

    Optional<Product> findByProductNameAndProcessStep_Sequence(String name, Long sequence);

    List<Product> findByProductNameStartingWith(String prefix); // 시퀀스 0 -> 1로 이동될때 냉동파우치(번호) 자동변경


}

