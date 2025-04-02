package com.project03.controller;

import com.project03.domain.ProcessStep;
import com.project03.domain.Product;
import com.project03.repository.ProcessStepRepository;
import com.project03.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/simulation")
@RequiredArgsConstructor
public class SimulationController {

    private final ProductRepository productRepository;
    private final ProcessStepRepository processStepRepository;

    // simulateStep 메서드 내부에서 지역 변수로 선언
    int movedQuantity = 0;
    int totalQuantity = 0;


    @GetMapping("/step/{from}/next")
    public ResponseEntity<Map<String, Object>> simulateStep(@PathVariable Long from) {
        Long next = from + 1;

        // 다음 공정 존재 확인
        ProcessStep nextStep = processStepRepository.findBySequence(next)
                .orElseThrow(() -> new RuntimeException("다음 공정이 없습니다."));

        // 제품 이동 로그: key = "냉동파우치 9 → 시퀀스 2", value = [누적이동량, 총량]
        Map<String, int[]> logMap = new LinkedHashMap<>();

        // 전체 수량 재계산
        List<Product> fromProducts = productRepository.findByProcessStep_Sequence(from);
        List<Product> toProducts = productRepository.findByProcessStep_Sequence(next);

        int totalQuantity = fromProducts.stream()
                .filter(p -> p.getQuantity() != null)
                .mapToInt(p -> p.getQuantity().intValue())
                .sum() + toProducts.stream()
                .filter(p -> p.getQuantity() != null)
                .mapToInt(p -> p.getQuantity().intValue())
                .sum();

        int movedQuantity = toProducts.stream()
                .filter(p -> p.getQuantity() != null)
                .mapToInt(p -> p.getQuantity().intValue())
                .sum();

        // 1개 제품 10개만 이동
        for (Product p : fromProducts) {
            if (p.getQuantity() >= 10) {
                p.setQuantity(p.getQuantity() - 10L);

                Optional<Product> existing = productRepository
                        .findByProductNameAndProcessStep_Sequence(p.getProductName(), nextStep.getSequence());

                if (from == 3) {
                    //  3 → 4 포장 로직: 10개 → 1개
                    existing.ifPresentOrElse(existingProduct -> {
                        existingProduct.setQuantity(existingProduct.getQuantity() + 1L);
                        productRepository.save(existingProduct);
                    }, () -> {
                        Product moved = new Product();
                        moved.setProductName(p.getProductName());
                        moved.setDescription(p.getDescription());
                        moved.setManufactureDate(p.getManufactureDate());
                        moved.setQuantity(1L);  // 낱개 포장
                        moved.setMaterialDescription(p.getMaterialDescription());
                        moved.setProcessStep(nextStep);
                        productRepository.save(moved);
                    });
                } else {
                    //  기존 공정 로직 (10개 → 10개)
                    if (existing.isPresent()) {
                        Product exist = existing.get();
                        exist.setQuantity(exist.getQuantity() + 10L);
                        exist.setMaterialDescription(p.getMaterialDescription());
                        productRepository.save(exist);
                    } else {
                        Product moved = new Product();
                        moved.setProductName(p.getProductName());
                        moved.setDescription(p.getDescription());
                        moved.setManufactureDate(p.getManufactureDate());
                        moved.setQuantity(10L);
                        moved.setMaterialDescription(p.getMaterialDescription());
                        moved.setProcessStep(nextStep);
                        productRepository.save(moved);
                    }
                }

                // 남은 수량 0이면 삭제
                if (p.getQuantity() == 0) {
                    productRepository.delete(p);
                } else {
                    productRepository.save(p);
                }

                // 로그 저장
                Long nowMoved = productRepository
                        .findByProductNameAndProcessStep_Sequence(p.getProductName(), nextStep.getSequence())
                        .map(Product::getQuantity).orElse(0L);

                Long originalTotal = nowMoved + p.getQuantity(); // 현재 수량 + 이동 수량 = 원래 총량

                String key = p.getProductName() + " → 시퀀스 " + next + " 완료";
                int[] progress;

                if (from == 3) {
                    // 3→4는 10개 단위가 1개 포장 → 로그 기준은 "1 / 10"
                    int movedBox = nowMoved.intValue(); // 예: 1
                    progress = new int[]{movedBox, 10}; // 10개 포장 기준
                } else {
                    int total = nowMoved.intValue() + p.getQuantity().intValue();
                    progress = new int[]{nowMoved.intValue(), total};
                }

                logMap.put(key, progress);

                break; // 1초에 한 제품만
            }
        }

        int progress = totalQuantity == 0 ? 0 : (movedQuantity * 100 / totalQuantity);

        // 결과 데이터 구성
        Map<String, Object> result = new HashMap<>();
        result.put("progress", progress);
        result.put("movedQuantity", movedQuantity);
        result.put("totalQuantity", totalQuantity);

        List<String> logs = new ArrayList<>();
        for (Map.Entry<String, int[]> entry : logMap.entrySet()) {
            int[] value = entry.getValue();
            logs.add(entry.getKey() + " (" + value[0] + " / " + value[1] + ")");
        }
        result.put("log", logs);

        return ResponseEntity.ok(result);
    }
    //특정 시퀀스 제품 리스트
    @GetMapping("/products/by-sequence/{sequence}")
    public ResponseEntity<List<String>> getProductsBySequence(@PathVariable Long sequence) {
        List<String> names = productRepository.findByProcessStep_Sequence(sequence).stream()
                .map(Product::getProductName)
                .distinct()
                .toList();

        return ResponseEntity.ok(names);
    }

}

