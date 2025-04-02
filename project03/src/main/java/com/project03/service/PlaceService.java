package com.project03.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project03.dto.PlaceDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceService {

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public List<PlaceDTO> searchPlaces(String query) {
        String url = "https://dapi.kakao.com/v2/local/search/keyword.json?query=" + query;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);
        headers.set("os", "Web");
        headers.set("origin", "http://localhost:8084");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return parseResponse(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<PlaceDTO> parseResponse(String responseBody) {
        List<PlaceDTO> places = new ArrayList<>();

        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode documents = rootNode.path("documents");

            for (JsonNode document : documents) {
                String placeName = document.path("place_name").asText();
                String address = document.path("address_name").asText();
                String roadAddress = document.path("road_address_name").asText();
                double latitude = document.path("y").asDouble();
                double longitude = document.path("x").asDouble();

                if (roadAddress.isEmpty()) {
                    roadAddress = address;
                }

                places.add(new PlaceDTO(
                        placeName,
                        address,
                        roadAddress,
                        latitude,
                        longitude
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return places;
    }
}
