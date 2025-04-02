package com.project03.repository;

import com.project03.domain.Material;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialRepository extends JpaRepository<Material, Long> {
    // 필요 시 커스텀 메서드 작성 가능
}
