package com.hsu.simcar.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.hsu.simcar.dto.CarRegistrationRequest;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member seller;
    
    private String type; // 차종
    private Long price; // 금액
    private String brand; // 브랜드
    private String model; // 모델명
    private Integer productionYear; // 연식
    private Integer mileage; // 주행거리
    private String fuelType; // 연료
    private String imageUrl; // 사진
    private String carNumber; // 차량번호
    private Integer insuranceHistory; // 보험이력
    private Integer inspectionHistory; // 성능점검 이력
    private String color; // 색상
    private String transmission; // 변속기
    private String region; // 판매지역
    private String contactNumber; // 판매자 연락처
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public void update(CarRegistrationRequest request) {
        this.type = request.getType();
        this.price = request.getPrice();
        this.brand = request.getBrand(); 
        this.model = request.getModel();
        this.productionYear = request.getYear();
        this.mileage = request.getMileage();
        this.fuelType = request.getFuelType();
        this.imageUrl = request.getImageUrl();
        this.carNumber = request.getCarNumber();
        this.insuranceHistory = request.getInsuranceHistory();
        this.inspectionHistory = request.getInspectionHistory();
        this.color = request.getColor();
        this.transmission = request.getTransmission();
        this.region = request.getRegion();
        this.contactNumber = request.getContactNumber();
    }
}