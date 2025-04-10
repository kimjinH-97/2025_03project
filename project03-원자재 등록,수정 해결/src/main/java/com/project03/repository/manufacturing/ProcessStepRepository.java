package com.project03.repository.manufacturing;

import com.project03.domain.ProcessStep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProcessStepRepository extends JpaRepository<ProcessStep, Long> {
    Optional<ProcessStep> findBySequence(Long sequence); //공정 번호로 조회.
}

