package com.example.Maptest.controller;

import com.example.Maptest.dto.PlaceInfo;
import com.example.Maptest.service.PlaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;

    // 기본 페이지로 이동
    @GetMapping("/")
    public String home(Model model) {
        return "search"; // 'search'는 Thymeleaf 템플릿 페이지 이름으로 가정
    }

    // 장소 검색 요청
    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<List<PlaceInfo>> searchPlaces(
            @RequestParam(value = "query") String query) {

        try {
            log.debug("장소 검색 요청 - 쿼리: {}", query);

            // 쿼리 검증
            if (query == null || query.trim().isEmpty()) {
                log.warn("쿼리가 비어 있음");
                return ResponseEntity.badRequest().body(null);
            }

            // 장소 검색
            List<PlaceInfo> places = placeService.searchPlaces(query);

            // 검색 결과가 없을 경우 처리
            if (places.isEmpty()) {
                log.info("검색 결과 없음 - 쿼리: {}", query);
                return ResponseEntity.noContent().build(); // 204 No Content 반환
            }

            log.info("검색 완료 - 결과 개수: {}", places.size());
            return ResponseEntity.ok(places); // 200 OK로 장소 목록 반환

        } catch (Exception e) {
            log.error("장소 검색 오류 - 쿼리: {}, 오류: {}", query, e.getMessage());
            return ResponseEntity.internalServerError().body(null); // 500 Internal Server Error 반환
        }
    }
}
