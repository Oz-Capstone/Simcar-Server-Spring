package com.hsu.simcar.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AIPredictionRequest {
    private int brand;        // 브랜드 인덱스
    private int model;        // 모델 인덱스
    private float price;      // 가격
    private int productionYear; // 연식
    private float mileage;     // 주행거리
    private int has_image;     // 이미지 존재 여부
    private int insuranceHistory;   // 보험 이력
    private int inspectionHistory;  // 검사 이력 
    private int region;       // 지역 인덱스
}
