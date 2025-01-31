// src/main/java/com/hsu/simcar/dto/CarDiagnosisResponse.java
package com.hsu.simcar.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CarDiagnosisResponse {
    private Long carId;
    private Integer reliabilityScore; // 0-100 
    private String evaluationComment;
}