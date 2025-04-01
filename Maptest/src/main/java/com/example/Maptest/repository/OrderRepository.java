package com.example.Maptest.repository;

import com.example.Maptest.dto.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCompanyName(String companyName);
    List<Order> findByDeparture_Address(String address);
    List<Order> findByDestination_Address(String address);
    List<Order> findByDeparture_LatitudeAndDeparture_Longitude(double latitude, double longitude);
    List<Order> findByDestination_LatitudeAndDestination_Longitude(double latitude, double longitude);
}
