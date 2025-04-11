package com.project03.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RouteResponseDTO {
    private Long id;
    private String startAddress;  // 출발지 주소
    private String endAddress;    // 도착지 주소
    private double distance;      // 거리 (km)
    private int duration;         // 시간 (초)
}