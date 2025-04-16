package com.project03.service.manufacturing;

import com.project03.domain.Order;
import com.project03.domain.Warehouse;
import com.project03.dto.orders.OrderPageRequestDTO;
import com.project03.dto.orders.OrderPageResponseDTO;
import com.project03.entity.Route;
import com.project03.repository.kakaoapi.RouteRepository;
import com.project03.repository.manufacturing.OrderRepository;
import com.project03.repository.manufacturing.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

        warehouse.setOrdered(true);
        warehouseRepository.save(warehouse);
        orderRepository.save(order);
    }

    // 주문 저장
    public void saveOrder(Order order) {
        orderRepository.save(order);
    }

    // 주문 상태 업데이트
    public void updateStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다"));

        order.setStatus(newStatus); // 상태 변경
        order.setUpdatedAt(LocalDateTime.now());  // 상태 변경 시간 갱신

        orderRepository.save(order);  // 변경 사항 저장
    }

    // 단일 주문 조회
    public Order findById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문을 찾을 수 없습니다."));
    }

    //  단순 검색
    public List<Order> searchOrders(String type, String keyword) {
        if ("pn".equals(type)) {
            return orderRepository.findByWarehouseProductNameContaining(keyword);
        } else if ("rt".equals(type)) {
            return orderRepository.findByRouteStartAddressContainingOrRouteEndAddressContaining(keyword, keyword);
        } else if ("st".equals(type)) {
            return orderRepository.findByStatusContaining(keyword);
        }
        return List.of();
    }

    //  페이징 + 검색 리스트
    public OrderPageResponseDTO<Order> getList(OrderPageRequestDTO requestDTO) {
        Pageable pageable = requestDTO.getPageable("id");
        Page<Order> result;

        String status = requestDTO.getStatus();
        String type = requestDTO.getType();
        String keyword = requestDTO.getKeyword();

        if (status != null && !status.isBlank()) {
            //  상태 검색이 우선
            result = orderRepository.findByStatusContaining(status, pageable);
        } else if (type != null && keyword != null && !keyword.isBlank()) {
            //  기존 type 검색 유지
            switch (type) {
                case "pn":
                    result = orderRepository.findByWarehouseProductNameContaining(keyword, pageable);
                    break;
                case "rt":
                    result = orderRepository.findByRouteStartAddressContainingOrRouteEndAddressContaining(keyword, keyword, pageable);
                    break;
                case "st":
                    result = orderRepository.findByStatusContaining(keyword, pageable);
                    break;
                default:
                    result = orderRepository.findAll(pageable);
            }
        } else {
            // 아무 필터도 없을 경우 전체 조회
            result = orderRepository.findAll(pageable);
        }

        return new OrderPageResponseDTO<>(requestDTO, result.getContent(), (int) result.getTotalElements());
    }
}

