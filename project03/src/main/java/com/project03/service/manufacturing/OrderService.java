package com.project03.service.manufacturing;

import com.project03.domain.Order;
import com.project03.domain.Warehouse;
import com.project03.entity.Route;
import com.project03.repository.kakaoapi.RouteRepository;
import com.project03.repository.manufacturing.OrderRepository;
import com.project03.repository.manufacturing.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final WarehouseRepository warehouseRepository;
    private final RouteRepository routeRepository;

    // 주문 등록
    public void createOrder(Long warehouseId, Long routeId) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new IllegalArgumentException("창고 정보가 없습니다"));

        // 이미 주문된 제품은 다시 등록하지 못하도록 체크
        if (warehouse.isOrdered()) {
            throw new IllegalStateException("이미 주문에 등록된 제품입니다.");
        }

        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new IllegalArgumentException("배송 경로가 없습니다"));

        Order order = new Order();
        order.setWarehouse(warehouse);
        order.setRoute(route);
        order.setOrderDate(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setStatus("대기");

        // 주문 등록 시 warehouse 상태 업데이트
        warehouse.setOrdered(true);
        warehouseRepository.save(warehouse);

        orderRepository.save(order);
    }


    public void saveOrder(Order order) {
        orderRepository.save(order);
    }
    // 전체 주문 조회
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // 주문 상태 업데이트
    public void updateStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다"));
        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }

    // ✅ 주문 ID로 단일 주문 조회
    public Order findById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문을 찾을 수 없습니다."));
    }
}

