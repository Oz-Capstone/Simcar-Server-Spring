// src/main/java/com/hsu/simcar/service/AICarDiagnosisService.java
package com.hsu.simcar.service;

import com.hsu.simcar.domain.Car;
import com.hsu.simcar.dto.AIPredictionRequest;
import com.hsu.simcar.dto.AIPredictionResponse;
import com.hsu.simcar.dto.CarDiagnosisResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AICarDiagnosisService implements AIDiagnosisService {
    private final RestTemplate restTemplate;
    
    @Value("${ai.service.url}")
    private String aiServiceUrl;

    @Override
    public CarDiagnosisResponse diagnose(Car car) {
        AIPredictionRequest request = AIPredictionRequest.builder()
            .brand(getBrandIndex(car.getBrand()))
            .model(getModelIndex(car.getModel()))
            .price(car.getPrice())
            .productionYear(car.getProductionYear())
            .mileage(car.getMileage())
            .has_image(car.getImageUrl() != null ? 1 : 0)
            .insuranceHistory(car.getInsuranceHistory())
            .inspectionHistory(car.getInspectionHistory())
            .region(getRegionIndex(car.getRegion()))
            .build();

        AIPredictionResponse aiResponse = restTemplate.postForObject(
            aiServiceUrl + "/predict",
            request,
            AIPredictionResponse.class
        );

        if (aiResponse == null || !aiResponse.isSuccess()) {
            throw new RuntimeException("AI 서비스 응답 실패");
        }

        // 사기 확률을 신뢰도 점수로 변환 (100 - 사기확률*100)
        int reliabilityScore = (int) Math.round(100 - (aiResponse.getFraud_probability() * 100));

        return CarDiagnosisResponse.builder()
            .carId(car.getId())
            .reliabilityScore(reliabilityScore)
            .evaluationComment(generateComment(reliabilityScore))
            .build();
    }

    private String generateComment(int score) {
        if (score >= 90) return "매우 신뢰할 수 있는 매물입니다.";
        else if (score >= 70) return "신뢰할 수 있는 매물입니다.";
        else if (score >= 50) return "주의가 필요한 매물입니다.";
        else return "신뢰도가 낮은 매물입니다. 각별한 주의가 필요합니다.";
    }

    private int getBrandIndex(String brand) {
        return switch (brand.toLowerCase()) {
            case "hyundai" -> 0;
            case "toyota" -> 1;
            case "honda" -> 2;
            case "ford" -> 3;
            case "bmw" -> 4;
            case "mercedes" -> 5;
            case "tesla" -> 6;
            default -> throw new IllegalArgumentException("Unknown brand: " + brand);
        };
    }

    private int getModelIndex(String model) {
        return switch (model.toLowerCase()) {
            case "elantra", "tucson" -> 0; // Hyundai
            case "corolla", "camry", "rav4" -> 1; // Toyota
            case "civic", "cr-v" -> 2; // Honda
            case "f-150", "explorer" -> 3; // Ford
            case "x3", "x5" -> 4; // BMW
            case "e-class" -> 5; // Mercedes
            case "model 3", "model y" -> 6; // Tesla
            default -> throw new IllegalArgumentException("Unknown model: " + model);
        };
    }

    private int getRegionIndex(String region) {
        return switch (region) {
            case "Seoul" -> 0;
            case "Busan" -> 1;
            case "Daegu" -> 2;
            case "Incheon" -> 3;
            case "Gwangju" -> 4;
            case "Daejeon" -> 5;
            case "Ulsan" -> 6;
            case "Sejong" -> 7;
            case "Gyeonggi" -> 8;
            default -> throw new IllegalArgumentException("Unknown region: " + region);
        };
    }
}