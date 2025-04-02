package com.project03.Delivery.repository;

import com.project03.Delivery.entity.DeliveryInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryInfoRepository extends JpaRepository<DeliveryInfo, Long> {
    // 필요한 경우 추가적인 쿼리 메서드를 정의할 수 있습니다.
}
