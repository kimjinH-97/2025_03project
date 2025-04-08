package com.project03.service.manufacturing;

import com.project03.domain.Warehouse;
import com.project03.repository.manufacturing.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WarehouseService {
    @Autowired
    private WarehouseRepository warehouseRepository;

    public List<Warehouse> getUnorderedWarehouses() {
        return warehouseRepository.findByIsOrderedFalse();
    }


    public void markAsOrdered(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("창고 정보를 찾을 수 없습니다."));
        warehouse.setOrdered(true);
        warehouseRepository.save(warehouse);
    }
}



