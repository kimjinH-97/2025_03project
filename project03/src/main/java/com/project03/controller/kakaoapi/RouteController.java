package com.project03.controller.kakaoapi;

import com.project03.dto.RouteDTO;
import com.project03.dto.RouteResponseDTO;
import com.project03.service.kakaoapi.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @PostMapping("/save")
    public ResponseEntity<String> saveRoute(@RequestBody RouteDTO routeDTO) {
        routeService.saveRoute(routeDTO);
        return ResponseEntity.ok("경로 저장 완료");
    }

    @GetMapping("/previous")
    public ResponseEntity<List<RouteResponseDTO>> getPreviousRoutes() {
        List<RouteResponseDTO> routes = routeService.getAllRoutes();
        return ResponseEntity.ok(routes);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRoute(@PathVariable Long id) {
        boolean deleted = routeService.deleteRouteById(id);
        if (deleted) {
            return ResponseEntity.ok("삭제 성공");
        } else {
            return ResponseEntity.status(500).body("삭제 실패");
        }
    }
}
