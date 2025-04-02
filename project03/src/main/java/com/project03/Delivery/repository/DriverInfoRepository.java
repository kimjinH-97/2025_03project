package com.project03.Delivery.repository;

import com.project03.Delivery.entity.DriverInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverInfoRepository extends JpaRepository<DriverInfo, Long> {
    // 필요한 경우 추가적인 쿼리 메서드를 정의할 수 있습니다.
}
