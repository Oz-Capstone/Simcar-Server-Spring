package com.hsu.simcar.dto;

import com.hsu.simcar.domain.Car;

import lombok.Data;
import java.util.List;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Data
public class CarDetailResponse {
    private Long id;
    private String type;
    private Long price;
    private String brand;
    private String model;
    private Integer year;
    private Integer mileage;
    private String fuelType;
    private List<CarImageResponse> images;
    private String carNumber;
    private Integer insuranceHistory;
    private Integer inspectionHistory;
    private String color;
    private String transmission;
    private String region;
    private String contactNumber;
    private String sellerName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    public static class CarImageResponse {
        private Long id;
        private String originalFileName;
        private String filePath;
        private boolean isThumbnail;
    }

    public static CarDetailResponse from(Car car) {
        CarDetailResponse response = new CarDetailResponse();
        response.setId(car.getId());
        response.setType(car.getType());
        response.setPrice(car.getPrice());
        response.setBrand(car.getBrand());
        response.setModel(car.getModel());
        response.setYear(car.getProductionYear());
        response.setMileage(car.getMileage());
        response.setFuelType(car.getFuelType());
        
        // 이미지 정보 변환
        response.setImages(car.getImages().stream()
            .map(image -> {
                CarImageResponse imageResponse = new CarImageResponse();
                imageResponse.setId(image.getId());
                imageResponse.setOriginalFileName(image.getOriginalFileName());
                imageResponse.setFilePath(image.getFilePath());
                imageResponse.setThumbnail(image.isThumbnail());
                return imageResponse;
            })
            .collect(Collectors.toList()));

        response.setCarNumber(car.getCarNumber());
        response.setInsuranceHistory(car.getInsuranceHistory());
        response.setInspectionHistory(car.getInspectionHistory());
        response.setColor(car.getColor());
        response.setTransmission(car.getTransmission());
        response.setRegion(car.getRegion());
        response.setContactNumber(car.getContactNumber());
        response.setSellerName(car.getSeller().getName());
        response.setCreatedAt(car.getCreatedAt());
        response.setUpdatedAt(car.getUpdatedAt());
        return response;
    }
}