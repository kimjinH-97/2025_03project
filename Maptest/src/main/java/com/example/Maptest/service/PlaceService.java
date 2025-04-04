package com.example.Maptest.service;

import com.example.Maptest.dto.PlaceInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceService {


    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;


    public List<PlaceInfo> searchPlaces(String query) {
        String url = "https://dapi.kakao.com/v2/local/search/keyword.json?query=" + query;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);
        headers.set("os", "Web");
        headers.set("origin", "http://localhost:8080");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return parseResponse(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<PlaceInfo> parseResponse(String responseBody) {
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode documents = rootNode.path("documents");

            return StreamSupport.stream(documents.spliterator(), false)
                    .map(this::createPlaceInfo)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.warn("API 응답 파싱 실패 - {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private PlaceInfo createPlaceInfo(JsonNode document) {
        return PlaceInfo.builder()
                .placeName(document.path("place_name").asText())
                .address(document.path("address_name").asText())
                .roadAddress(document.path("road_address_name").asText().isEmpty() ?
                        document.path("address_name").asText() :
                        document.path("road_address_name").asText())
                .latitude(document.path("y").asDouble())
                .longitude(document.path("x").asDouble())
                .build();
    }

    private void validateQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            log.warn("잘못된 검색어 입력: {}", query);
            throw new IllegalArgumentException("검색어는 필수 입력값입니다.");
        }
    }
}
