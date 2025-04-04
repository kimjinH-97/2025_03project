package com.example.Maptest.dto;

import com.example.Maptest.dto.PlaceInfo;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "placeName", column = @Column(name = "departure_place_name")),
            @AttributeOverride(name = "address", column = @Column(name = "departure_address")),
            @AttributeOverride(name = "roadAddress", column = @Column(name = "departure_road_address")),
            @AttributeOverride(name = "latitude", column = @Column(name = "departure_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "departure_longitude"))
    })
    private PlaceInfo departure;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "placeName", column = @Column(name = "destination_place_name")),
            @AttributeOverride(name = "address", column = @Column(name = "destination_address")),
            @AttributeOverride(name = "roadAddress", column = @Column(name = "destination_road_address")),
            @AttributeOverride(name = "latitude", column = @Column(name = "destination_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "destination_longitude"))
    })
    private PlaceInfo destination;


}