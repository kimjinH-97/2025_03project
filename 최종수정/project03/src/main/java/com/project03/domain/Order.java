package com.project03.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project03.entity.Route;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "orders") // 복수형 테이블 사용 중
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 창고 → 어떤 제품이 실렸는가
    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    // 배송 경로 → 어디서 어디로 가는가
    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;

    private String status; // 대기, 배송중, 도착 등

    private LocalDateTime orderDate; // 주문 생성일

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt; // 상태 변경 시간 (상태가 바뀔 때마다 이 필드 업데이트)

}

