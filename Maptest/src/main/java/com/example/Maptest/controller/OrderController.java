package com.example.Maptest.controller;

import com.example.Maptest.dto.Order;
import com.example.Maptest.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    // 주문 생성
    @PostMapping
    @ResponseBody
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        try {
            Order createdOrder = orderService.createOrder(order);
            log.info("주문 생성 완료 - 주문 ID: {}", createdOrder.getId());
            return ResponseEntity.ok(createdOrder);
        } catch (Exception e) {
            log.error("주문 생성 실패 - 오류: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(null);
        }
    }

    // 주문 ID로 조회
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        try {
            Order order = orderService.getOrderById(id);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            log.error("주문 조회 실패 - 주문 ID: {}, 오류: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    // 회사 이름으로 주문 목록 조회
    @GetMapping("/company/{companyName}")
    @ResponseBody
    public ResponseEntity<List<Order>> getOrdersByCompanyName(@PathVariable String companyName) {
        List<Order> orders = orderService.getOrdersByCompanyName(companyName);
        return ResponseEntity.ok(orders);
    }

    // 출발지 주소로 주문 목록 조회
    @GetMapping("/departure/address")
    @ResponseBody
    public ResponseEntity<List<Order>> getOrdersByDepartureAddress(@RequestParam String address) {
        List<Order> orders = orderService.getOrdersByDepartureAddress(address);
        return ResponseEntity.ok(orders);
    }

    // 목적지 주소로 주문 목록 조회
    @GetMapping("/destination/address")
    @ResponseBody
    public ResponseEntity<List<Order>> getOrdersByDestinationAddress(@RequestParam String address) {
        List<Order> orders = orderService.getOrdersByDestinationAddress(address);
        return ResponseEntity.ok(orders);
    }

    // 출발지 좌표로 주문 목록 조회
    @GetMapping("/departure/coordinates")
    @ResponseBody
    public ResponseEntity<List<Order>> getOrdersByDepartureCoordinates(
            @RequestParam double latitude, @RequestParam double longitude) {
        List<Order> orders = orderService.getOrdersByDepartureCoordinates(latitude, longitude);
        return ResponseEntity.ok(orders);
    }

    // 목적지 좌표로 주문 목록 조회
    @GetMapping("/destination/coordinates")
    @ResponseBody
    public ResponseEntity<List<Order>> getOrdersByDestinationCoordinates(
            @RequestParam double latitude, @RequestParam double longitude) {
        List<Order> orders = orderService.getOrdersByDestinationCoordinates(latitude, longitude);
        return ResponseEntity.ok(orders);
    }
}
