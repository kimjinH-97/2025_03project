package com.project03.Delivery.service;

import com.project03.Delivery.entity.DeliveryInfo;
import com.project03.Delivery.entity.DeliveryRoute;
import com.project03.Delivery.entity.DriverInfo;
import com.project03.Delivery.repository.DeliveryInfoRepository;
import com.project03.Delivery.repository.DeliveryRouteRepository;
import com.project03.Delivery.repository.DriverInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeliveryService {

    @Autowired
    private DeliveryInfoRepository deliveryInfoRepository;

    @Autowired
    private DeliveryRouteRepository deliveryRouteRepository;

    @Autowired
    private DriverInfoRepository driverInfoRepository;

    public void startDelivery(DeliveryInfo deliveryInfo, DeliveryRoute deliveryRoute, DriverInfo driverInfo) {
        // 배송정보 저장
        deliveryInfo.setStatus("배송중");
        deliveryInfoRepository.save(deliveryInfo);

        // 경로정보 저장
        deliveryRoute.setDeliveryInfo(deliveryInfo);
        deliveryRouteRepository.save(deliveryRoute);

        // 기사정보 저장
        driverInfo.setAssignedDelivery(deliveryInfo);
        driverInfoRepository.save(driverInfo);
    }

    public void completeDelivery(Long deliveryId) {
        // 배송 상태를 '배송완료'로 업데이트
        DeliveryInfo deliveryInfo = deliveryInfoRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("배송정보가 없습니다"));

        deliveryInfo.setStatus("배송완료");
        deliveryInfoRepository.save(deliveryInfo);
    }
}