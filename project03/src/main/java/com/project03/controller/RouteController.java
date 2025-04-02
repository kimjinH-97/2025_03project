package com.project03.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
public class RouteController {

    @GetMapping("/route")
    public String showRoutePage() {
        return "test";  // route.html 페이지 반환
    }

    @PostMapping("/calculateRoute")
    @ResponseBody
    public String calculateRoute(@RequestParam String start,
                                 @RequestParam String end) {
        // 출발지와 목적지를 받아서 카카오 지도 API로 경로 계산
        // 서버 측에서 경로를 계산하여 클라이언트에 응답
        // 이 예시는 카카오 API와의 통신을 실제로 하진 않고, 예시 데이터를 반환합니다.

        String dummyResponse = "{"
                + "\"status\": \"OK\","
                + "\"duration\": \"15분\","
                + "\"arrivalTime\": \"2025-03-25T14:30:00\""
                + "}";

        // 실제로는 카카오 API를 호출하여 JSON 데이터를 반환받고, 클라이언트에 반환
        return dummyResponse;
    }
}
