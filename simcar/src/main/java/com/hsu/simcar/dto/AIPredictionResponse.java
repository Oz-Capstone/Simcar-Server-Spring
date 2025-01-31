package com.hsu.simcar.dto;

import lombok.Data;

@Data
public class AIPredictionResponse {
    private boolean success;
    private double fraud_probability;
    private String probability_percentage;
}
