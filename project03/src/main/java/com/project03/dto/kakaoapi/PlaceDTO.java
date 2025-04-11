package com.project03.dto.kakaoapi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceDTO {
    private String placeName;   // 장소명
    private String address;     // 지번 주소
    private String roadAddress; // 도로명 주소
    private double latitude;    // 위도
    private double longitude;   // 경도
}
