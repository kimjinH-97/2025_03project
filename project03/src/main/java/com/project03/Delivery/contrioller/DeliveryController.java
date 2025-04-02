package com.project03.Delivery.controller;

import com.project03.Delivery.entity.DeliveryInfo;
import com.project03.Delivery.entity.DeliveryRoute;
import com.project03.Delivery.entity.DriverInfo;
import com.project03.Delivery.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/delivery")
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;

    @PostMapping("/start")
    public String startDelivery(@RequestBody DeliveryInfo deliveryInfo,
                                @RequestBody DeliveryRoute deliveryRoute,
                                @RequestBody DriverInfo driverInfo) {
        deliveryService.startDelivery(deliveryInfo, deliveryRoute, driverInfo);
        return "배송이 시작되었습니다.";
    }

    @PostMapping("/complete/{id}")
    public String completeDelivery(@PathVariable Long id) {
        deliveryService.completeDelivery(id);
        return "배송이 완료되었습니다.";
    }
}
