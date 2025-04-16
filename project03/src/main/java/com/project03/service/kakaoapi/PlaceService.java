package com.project03.service.kakaoapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project03.dto.kakaoapi.PlaceDTO;
import com.project03.dto.kakaoapi.RouteDTO;
import com.project03.entity.Place;
import com.project03.entity.Route;
import com.project03.repository.kakaoapi.PlaceRepository;
import com.project03.repository.kakaoapi.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlaceService {

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final PlaceRepository placeRepository;
    private final RouteRepository routeRepository;

    // 장소 검색
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

                places.add(new PlaceDTO(placeName, address, roadAddress, latitude, longitude));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return places;
    }

    // 출발지 or 목적지 저장 + 장소중복 저장 방지..!
    @Transactional
    public void saveSelectedPlaces(RouteDTO request) {
        if (request.getStart() == null || request.getEnd() == null) {
            throw new IllegalArgumentException("출발지 또는 목적지 정보가 없습니다.");
        }

        System.out.println("Start DTO: " + request.getStart());
        System.out.println("End DTO: " + request.getEnd());

        Place start = findOrCreatePlace(request.getStart());
        Place end = findOrCreatePlace(request.getEnd());

        Route route = Route.builder()
                .startAddress(start.getAddress())
                .endAddress(end.getAddress())
                .build();

        routeRepository.save(route);
    }



    //기존 장소 조회 or 신규 저장
    private Place findOrCreatePlace(PlaceDTO dto) {
        if (dto == null || dto.getPlaceName() == null || dto.getAddress() == null) {
            throw new IllegalArgumentException("장소 정보가 올바르지 않습니다.");
        }

        System.out.println("찾는 장소: " + dto.getPlaceName() + ", " + dto.getAddress());

        Optional<Place> existingPlace = placeRepository.findByPlaceNameAndAddress(dto.getPlaceName(), dto.getAddress());

        System.out.println("DB 조회 결과: " + existingPlace.orElse(null));

        return existingPlace.orElseGet(() -> placeRepository.save(
                Place.builder()
                        .placeName(dto.getPlaceName())
                        .address(dto.getAddress())
                        .roadAddress(dto.getRoadAddress())
                        .latitude(dto.getLatitude())
                        .longitude(dto.getLongitude())
                        .build()
        ));
    }

}
