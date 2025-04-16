package com.project03.dto.orders;

import lombok.Data;
import java.util.List;
import java.util.function.Function;

@Data
public class OrderPageResponseDTO<T> {

    private int page;
    private int size;
    private int total;

    private int start;
    private int end;
    private boolean prev;
    private boolean next;

    private List<T> dtoList;

    public OrderPageResponseDTO(OrderPageRequestDTO requestDTO, List<T> dtoList, int total) {
        this.page = requestDTO.getPage();
        this.size = requestDTO.getSize();
        this.total = total;
        this.dtoList = dtoList;

        int tempEnd = (int)(Math.ceil(page / 10.0)) * 10;
        this.start = tempEnd - 9;
        this.end = Math.min((int)(Math.ceil((total * 1.0) / size)), tempEnd);

        this.prev = this.start > 1;
        this.next = total > this.end * size;
    }
}
