package com.project03.dto.orders;

import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
public class OrderPageRequestDTO {

    private int page = 1;         // 현재 페이지 (기본값 1)
    private int size = 10;        // 페이지당 항목 수 (기본값 10)
    private String type;          // 검색 타입 (예: productName)
    private String keyword;       // 검색 키워드

    private String status;        // 주문 상태 필터

    // Spring Data Pageable 객체로 변환
    public Pageable getPageable(String... props) {
        return PageRequest.of(this.page - 1, this.size, Sort.by(props).descending());
    }
}
