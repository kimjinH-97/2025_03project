package com.example.finalproject.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LocationController {
    @GetMapping("/location")
    public String getLocation(){
        // 위치 반환하는 로직을 여기에 추가
        return "현재 위치";

    }
}
