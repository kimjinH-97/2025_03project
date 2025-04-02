package com.project03.Delivery.entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class DeliveryInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId;
    private String sender;
    private String receiver;
    private String status; // '대기', '배송중', '배송완료' 등의 상태
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "deliveryInfo", cascade = CascadeType.ALL)
    private DeliveryRoute deliveryRoute;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private DriverInfo driverInfo;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public DeliveryRoute getDeliveryRoute() {
        return deliveryRoute;
    }

    public void setDeliveryRoute(DeliveryRoute deliveryRoute) {
        this.deliveryRoute = deliveryRoute;
    }

    public DriverInfo getDriverInfo() {
        return driverInfo;
    }

    public void setDriverInfo(DriverInfo driverInfo) {
        this.driverInfo = driverInfo;
    }
}
