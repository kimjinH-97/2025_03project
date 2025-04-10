package com.project03.repository.kakaoapi;

import com.project03.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    Optional<Place> findByPlaceNameAndAddress(String placeName, String address);
}
