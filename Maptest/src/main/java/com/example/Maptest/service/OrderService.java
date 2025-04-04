package com.example.Maptest.service;

import com.example.Maptest.dto.Order;
import com.example.Maptest.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public Order createOrder(Order order) {
        validateOrder(order);
        Order savedOrder = orderRepository.save(order);
        log.info("주문 생성 완료 - 주문 ID: {}", savedOrder.getId());
        return savedOrder;
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다 (ID: " + orderId + ")"));
    }

    public List<Order> getOrdersByCompanyName(String companyName) {
        return orderRepository.findByCompanyName(companyName);
    }

    public List<Order> getOrdersByDepartureAddress(String address) {
        return orderRepository.findByDeparture_Address(address);
    }

    public List<Order> getOrdersByDestinationAddress(String address) {
        return orderRepository.findByDestination_Address(address);
    }

    public List<Order> getOrdersByDepartureCoordinates(double latitude, double longitude) {
        return orderRepository.findByDeparture_LatitudeAndDeparture_Longitude(latitude, longitude);
    }

    public List<Order> getOrdersByDestinationCoordinates(double latitude, double longitude) {
        return orderRepository.findByDestination_LatitudeAndDestination_Longitude(latitude, longitude);
    }

    private void validateOrder(Order order) {
        if (order.getDeparture() == null || order.getDestination() == null) {
            throw new RuntimeException("출발지와 목적지는 필수 입력값입니다.");
        }
    }
}
