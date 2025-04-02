package com.project03.Delivery.entity;

import jakarta.persistence.*;

@Entity
public class DriverInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String phoneNumber;
    private String vehicleNumber;

    @ManyToOne
    @JoinColumn(name = "assigned_delivery_id")
    private DeliveryInfo assignedDelivery;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public DeliveryInfo getAssignedDelivery() {
        return assignedDelivery;
    }

    public void setAssignedDelivery(DeliveryInfo assignedDelivery) {
        this.assignedDelivery = assignedDelivery;
    }
}