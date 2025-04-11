package com.project03.service.kakaoapi;

import com.project03.dto.PlaceDTO;
import com.project03.dto.RouteDTO;
import com.project03.dto.RouteResponseDTO;
import com.project03.entity.Place;
import com.project03.entity.Route;
import com.project03.repository.kakaoapi.PlaceRepository;
import com.project03.repository.kakaoapi.RouteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    // 🔹 전체 경로 조회 (RouteResponseDTO 리스트로 변환)
    public List<RouteResponseDTO> getAllRoutes() {
        return routeRepository.findAll().stream()
                .map(route -> new RouteResponseDTO(

                        route.getId(),
                        route.getStartAddress(),
                        route.getEndAddress(),
                        route.getDistance(),
                        route.getDuration()
                ))
                .collect(Collectors.toList());
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

    public boolean deleteRouteById(Long id) {
        try {
            routeRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}