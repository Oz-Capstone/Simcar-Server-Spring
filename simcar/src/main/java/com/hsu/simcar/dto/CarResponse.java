package com.hsu.simcar.dto;

import com.hsu.simcar.domain.Car;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CarResponse {
    private Long id;
    private String type;
    private Long price; 
    private String brand;
    private String model;
    private Integer year;
    private String imageUrl;
    private String region;
    private LocalDateTime createdAt;

    public static CarResponse from(Car car) {
        CarResponse response = new CarResponse();
        response.setId(car.getId());
        response.setType(car.getType());
        response.setPrice(car.getPrice());
        response.setBrand(car.getBrand());
        response.setModel(car.getModel());
        response.setYear(car.getProductionYear());
        response.setImageUrl(car.getRepresentativeImageUrl());
        response.setRegion(car.getRegion());
        response.setCreatedAt(car.getCreatedAt());
        return response;
    }
}