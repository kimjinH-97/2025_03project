package com.project03.controller;

import com.project03.dto.PlaceDTO;
import com.project03.dto.RouteDTO;
import com.project03.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;

    @GetMapping("/")
    public String home() {
        return "search";
    }

    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<List<PlaceDTO>> search(@RequestParam(value = "query", required = false) String query) {
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(placeService.searchPlaces(query));
    }

    // 🔹 출발지 & 목적지 저장
    @PostMapping("/saveRoute")
    @ResponseBody
    public ResponseEntity<String> saveRoute(@RequestBody RouteDTO request) {
        placeService.saveSelectedPlaces(request);
        return ResponseEntity.ok("출발지와 목적지가 저장되었습니다.");
    }
}
