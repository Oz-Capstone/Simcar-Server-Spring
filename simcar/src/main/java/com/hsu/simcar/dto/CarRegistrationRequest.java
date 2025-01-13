package com.hsu.simcar.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

@Data
public class CarRegistrationRequest {
    @NotBlank(message = "차종은 필수입니다")
    @Schema(description = "차종", example = "세단")
    private String type;
    
    @NotNull(message = "금액은 필수입니다")
    @Min(value = 0, message = "금액은 0보다 커야 합니다")
    @Schema(description = "금액", example = "20000000")
    private Long price;
    
    @NotBlank(message = "브랜드는 필수입니다")
    @Schema(description = "브랜드", example = "현대")
    private String brand;
    
    @NotBlank(message = "모델명은 필수입니다")
    @Schema(description = "모델명", example = "소나타")
    private String model;
    
    @NotNull(message = "연식은 필수입니다")
    @Schema(description = "연식", example = "2020")
    private Integer year;
    
    @NotNull(message = "주행거리는 필수입니다")
    @Schema(description = "주행거리", example = "50000")
    private Integer mileage;
    
    @NotBlank(message = "연료 종류는 필수입니다")
    @Schema(description = "연료 종류", example = "가솔린")
    private String fuelType;
    
    @NotBlank(message = "차량 사진은 필수입니다")
    @Schema(description = "차량 사진 URL", example = "http://example.com/car.jpg")
    private String imageUrl;
    
    @NotBlank(message = "차량번호는 필수입니다")
    @Schema(description = "차량번호", example = "12가 3456")
    private String carNumber;
    
    private String insuranceHistory;
    private String inspectionHistory;
    
    @NotBlank(message = "색상은 필수입니다")
    @Schema(description = "색상", example = "검정")
    private String color;
    
    @NotBlank(message = "변속기 종류는 필수입니다")
    @Schema(description = "변속기", example = "자동")
    private String transmission;
    
    @NotBlank(message = "판매지역은 필수입니다")
    @Schema(description = "판매지역", example = "서울")
    private String region;
    
    @NotBlank(message = "연락처는 필수입니다")
    @Schema(description = "판매자 연락처", example = "010-1234-5678")
    private String contactNumber;
}