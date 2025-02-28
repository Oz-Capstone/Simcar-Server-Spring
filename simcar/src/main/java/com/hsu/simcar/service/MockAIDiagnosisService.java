package com.hsu.simcar.service;

import com.hsu.simcar.domain.Car;
import com.hsu.simcar.dto.CarDiagnosisResponse;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service
public class MockAIDiagnosisService implements AIDiagnosisService {
    @Override
    public CarDiagnosisResponse diagnose(Car car) {
        // TODO: 실제 AI 서비스 연동 전까지 임시 응답 제공
        return CarDiagnosisResponse.builder()
                .carId(car.getId())
                .reliabilityScore(100)
                .evaluationComment("AI 진단 서비스 준비 중입니다.")
                .build();
    }
}