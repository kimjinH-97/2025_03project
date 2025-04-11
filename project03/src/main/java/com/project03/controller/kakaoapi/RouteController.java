package com.project03.controller.kakaoapi;

<<<<<<< HEAD
import com.project03.dto.kakaoapi.RouteDTO;
=======
import com.project03.dto.RouteDTO;
>>>>>>> eaf74e1e42fecc5340d4e4755cdb759bf43ef747
import com.project03.service.kakaoapi.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

