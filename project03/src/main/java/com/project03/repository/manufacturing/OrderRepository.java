package com.project03.repository.manufacturing;

import com.project03.domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    //필드 경로 기준
    Page<Order> findByWarehouseProductNameContaining(String keyword, Pageable pageable);

    Page<Order> findByRouteStartAddressContainingOrRouteEndAddressContaining(String keyword1, String keyword2, Pageable pageable);

    Page<Order> findByStatusContaining(String keyword, Pageable pageable);

    //단순 리스트용
    List<Order> findByWarehouseProductNameContaining(String keyword);

    List<Order> findByRouteStartAddressContainingOrRouteEndAddressContaining(String keyword1, String keyword2);

    List<Order> findByStatusContaining(String keyword);
}
