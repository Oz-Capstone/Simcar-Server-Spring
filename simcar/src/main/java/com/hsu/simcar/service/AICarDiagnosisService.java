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
            .has_image(!car.getImages().isEmpty() ? 1 : 0)
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
            case "hyundai", "현대" -> 0;
            case "kia", "기아" -> 1;
            case "genesis", "제네시스" -> 2;
            case "bmw", "비엠더블유" -> 3;
            case "benz", "벤츠" -> 4;
            case "audi", "아우디" -> 5;
            default -> throw new IllegalArgumentException("Unknown brand: " + brand);
        };
    }

    private int getModelIndex(String model) {
        return switch (model.toLowerCase()) {
            // Hyundai models
            case "grandeur", "그랜저", 
                "sonata", "소나타",
                "avante", "아반떼",
                "santafe", "싼타페" -> 0;
            // Kia models
            case "carnival", "카니발",
                "k5", "케이5",
                "sorento", "쏘렌토" -> 1;
            // Genesis models
            case "gv70", "지브이70",
                "g90", "지90" -> 2;
            // BMW models
            case "7series", "7시리즈",
                "x6", "엑스6" -> 3;
            // Benz models
            case "amg gt", "에이엠지 지티",
                "s-class", "s클래스" -> 4;
            // Audi models
            case "a5", "에이5",
                "a8", "에이8" -> 5;
            default -> throw new IllegalArgumentException("Unknown model: " + model);
        };
    }

    private int getRegionIndex(String region) {
        return switch (region) {
            case "Seoul", "서울" -> 0;
            case "Busan", "부산" -> 1;
            case "Daegu", "대구" -> 2;
            case "Incheon", "인천" -> 3;
            case "Gwangju", "광주" -> 4;
            case "Daejeon", "대전" -> 5;
            case "Ulsan", "울산" -> 6;
            case "Sejong", "세종" -> 7;
            case "Gyeonggi", "경기" -> 8;
            default -> throw new IllegalArgumentException("Unknown region: " + region);
        };
    }
}