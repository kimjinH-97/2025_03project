package com.project03.service.manufacturing;

import com.project03.domain.Material;
import com.project03.domain.ProcessStep;
import com.project03.domain.Product;
import com.project03.repository.manufacturing.MaterialRepository;
import com.project03.repository.manufacturing.ProcessStepRepository;
import com.project03.repository.manufacturing.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProcessStepRepository processStepRepository;
    private final MaterialRepository materialRepository;

    public ProductService(ProductRepository productRepository,
                          ProcessStepRepository processStepRepository,
                          MaterialRepository materialRepository) {
        this.productRepository = productRepository;
        this.processStepRepository = processStepRepository;
        this.materialRepository = materialRepository;
    }

    /**
     * 제품의 공정을 다음 단계로 이동
     */
    public void moveToNextStep(Long productId) {
        // 제품 가져오기
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("해당 제품을 찾을 수 없습니다."));

        Long currentStep = product.getProcessStep().getSequence(); // 현재 공정
        Long nextStep = currentStep + 1;

        // 다음 공정 조회
        ProcessStep nextProcess = processStepRepository.findBySequence(nextStep)
                .orElseThrow(() -> new RuntimeException("다음 공정이 없습니다. 마지막 단계입니다."));

        // 이름 자동 변경 로직 (0 → 1로 이동하는 경우에만)
        if (currentStep == 0) {
            // "냉동파우치 {숫자}" 형식 중 가장 큰 숫자 찾기
            List<Product> pouchList = productRepository.findByProductNameStartingWith("냉동파우치 ");
            long maxNo = pouchList.stream()
                    .map(p -> p.getProductName().replace("냉동파우치 ", ""))
                    .filter(s -> s.matches("\\d+"))
                    .mapToLong(Long::parseLong)
                    .max()
                    .orElse(0);

            product.setProductName("냉동파우치 " + (maxNo + 1));
        }

        // 공정 업데이트 후 저장
        product.setProcessStep(nextProcess);
        productRepository.save(product);

    }



    public void registerProductFromMaterial(Long materialId) {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("원자재를 찾을 수 없습니다."));

        Product product = new Product();
        product.setProductName(material.getMaterialName()); // 이름 복사
        product.setMaterialDescription(material.getMaterialDescription()); // 등급 복사
        product.setManufactureDate(new Date());
        product.setDescription("등록된 제품입니다.");
        product.setQuantity(Long.valueOf(material.getMaterialQuantity()));

        // 공정 0단계 설정
        ProcessStep zeroStep = processStepRepository.findBySequence(0L)
                .orElseThrow(() -> new RuntimeException("0단계 공정이 존재하지 않습니다."));

        product.setProcessStep(zeroStep);
        productRepository.save(product);
        materialRepository.delete(material); // 또는 수량 차감
    }

    @Transactional
    public boolean cancelProductToMaterial(Long productId) {
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("해당 제품을 찾을 수 없습니다."));

            // 시퀀스가 0단계가 아닐 경우 처리 중단
            if (product.getProcessStep() == null || product.getProcessStep().getSequence() != 0) {
                return false;
            }

            // 제품을 원자재로 복구
            Material material = new Material();
            material.setMaterialName(product.getProductName());
            material.setMaterialDescription(product.getMaterialDescription());
            material.setMaterialQuantity(String.valueOf(product.getQuantity()));

            // 사이즈 자동 설정
            Long size = switch (product.getMaterialDescription()) {
                case "소형" -> 100L;
                case "중형" -> 500L;
                case "대형" -> 1000L;
                default -> 0L;
            };
            material.setMaterialSize(String.valueOf(size));

            materialRepository.save(material);
            productRepository.delete(product);

            return true;
        } catch (Exception e) {
            // 콘솔에 상세 에러 로그 출력
            System.err.println("공정 취소 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


}


