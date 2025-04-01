package com.project03.controller;


import com.project03.dto.PlaceDTO;
import com.project03.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;

    // HTML 페이지 반환 (GET /)
    @GetMapping("/")
    public String home(Model model) {
        return "search";
    }

    // JSON API 반환 (GET /search)
    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<List<PlaceDTO>> search(
            @RequestParam(value = "query", required = false) String query) {

        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        List<PlaceDTO> places = placeService.searchPlaces(query);
        return ResponseEntity.ok(places);
    }
}