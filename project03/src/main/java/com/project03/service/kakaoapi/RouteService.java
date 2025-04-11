package com.project03.service.kakaoapi;

import com.project03.dto.kakaoapi.PlaceDTO;
import com.project03.dto.kakaoapi.RouteDTO;
import com.project03.entity.Place;
import com.project03.entity.Route;
import com.project03.repository.kakaoapi.PlaceRepository;
import com.project03.repository.kakaoapi.RouteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class RouteService {
    private final PlaceRepository placeRepository;
    private final RouteRepository routeRepository;

    @Transactional
    public void saveRoute(RouteDTO request) {
        // 🔹 출발지 & 목적지 저장 (중복 방지)
        Place start = findOrCreatePlace(request.getStart());
        Place end = findOrCreatePlace(request.getEnd());

        // 🔹 경로 저장
        Route route = Route.builder()
                .startAddress(start.getAddress())
                .endAddress(end.getAddress())
                .distance(request.getDistance())
                .duration(request.getDuration())
                .build();

        routeRepository.save(route);
    }

    private Place findOrCreatePlace(PlaceDTO dto) {
        if (dto == null || dto.getPlaceName() == null || dto.getAddress() == null) {
            throw new IllegalArgumentException("장소 정보가 올바르지 않습니다.");
        }

        return placeRepository.findByPlaceNameAndAddress(dto.getPlaceName(), dto.getAddress())
                .orElseGet(() -> placeRepository.save(
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

