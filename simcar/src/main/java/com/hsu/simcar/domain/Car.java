package com.hsu.simcar.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "image_order")
    @Builder.Default
    private List<CarImage> images = new ArrayList<>();
    
    private String type; // 차종
    private Long price; // 금액
    private String brand; // 브랜드
    private String model; // 모델명
    private Integer productionYear; // 연식
    private Integer mileage; // 주행거리
    private String fuelType; // 연료
    private String carNumber; // 차량번호
    private Integer insuranceHistory; // 보험이력
    private Integer inspectionHistory; // 성능점검 이력
    private String color; // 색상
    private String transmission; // 변속기
    private String region; // 판매지역
    private String contactNumber; // 판매자 연락처
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void addImage(CarImage image) {
        this.images.add(image);
        image.setCar(this);
    }

    public void removeImage(CarImage image) {
        this.images.remove(image);
        image.setCar(null);
    }

    public void updateImagesOrder(List<Long> imageIds) {
        // 입력받은 순서대로 정렬된 이미지 리스트 생성
        List<CarImage> sortedImages = imageIds.stream()
            .map(imageId -> this.images.stream()
                .filter(image -> image.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이미지입니다")))
            .collect(Collectors.toList());

        // 순서 정보 업데이트
        for (int i = 0; i < sortedImages.size(); i++) {
            CarImage image = sortedImages.get(i);
            image.setOrder(i);  // CarImage 엔티티에 order 필드 추가 필요
        }

        // 이미지 리스트 업데이트
        this.images.clear();
        this.images.addAll(sortedImages);
    }
    
    public String getRepresentativeImageUrl() {
        return images.stream()
            .filter(CarImage::isThumbnail)
            .findFirst()
            .map(CarImage::getFilePath)
            .orElse(null);
    }

    
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
        this.carNumber = request.getCarNumber();
        this.insuranceHistory = request.getInsuranceHistory();
        this.inspectionHistory = request.getInspectionHistory();
        this.color = request.getColor();
        this.transmission = request.getTransmission();
        this.region = request.getRegion();
        this.contactNumber = request.getContactNumber();
    }
}