package com.project03.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RouteDTO {
    private PlaceDTO start;  // 출발지 정보
    private PlaceDTO end;    // 목적지 정보
    private double distance;
    private int duration;
}
