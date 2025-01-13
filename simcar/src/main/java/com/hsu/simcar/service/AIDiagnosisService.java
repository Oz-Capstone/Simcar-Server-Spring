package com.hsu.simcar.service;

import com.hsu.simcar.domain.Car;
import com.hsu.simcar.dto.CarDiagnosisResponse;

public interface AIDiagnosisService {
    CarDiagnosisResponse diagnose(Car car);
}