package com.hsu.simcar.controller;

import com.hsu.simcar.dto.CarDetailResponse;
import com.hsu.simcar.dto.CarRegistrationRequest;
import com.hsu.simcar.dto.CarResponse;
import com.hsu.simcar.service.CarService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Car", description = "차량 관리 API")
@RequiredArgsConstructor
public class CarController {
    private final CarService carService;

    @Operation(summary = "차량 등록", description = "차량을 등록합니다.")
    @PostMapping("/cars")
    public ResponseEntity<Void> registerCar(
            @Valid @RequestBody CarRegistrationRequest request,
            HttpSession session) {
        Long sellerId = (Long) session.getAttribute("memberId");
        if (sellerId == null) {
            return ResponseEntity.status(401).build();
        }
        carService.registerCar(sellerId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "전체 차량 조회", description = "전체 차량 목록을 조회합니다.")
    @GetMapping("/cars")
    public ResponseEntity<List<CarResponse>> getAllCars() {
        return ResponseEntity.ok(carService.getAllCars());
    }

    @Operation(summary = "차량 상세 조회", description = "차량 ID로 상세 정보를 조회합니다.")
    @GetMapping("/cars/{carId}")
    public ResponseEntity<CarDetailResponse> getCarDetail(
        @Parameter(
            name = "carId",
            description = "차량 ID", 
            required = true,
            in = ParameterIn.PATH,
            example = "1"
        )
        @PathVariable("carId") Long carId) {
        return ResponseEntity.ok(carService.getCarDetail(carId));
    }

    @Operation(summary = "차량 정보 수정", description = "차량 ID로 차량 정보를 수정합니다.")
    @PutMapping("/cars/{carId}")
    public ResponseEntity<Void> updateCar(
            @Parameter(
                name = "carId",
                description = "차량 ID", 
                required = true,
                in = ParameterIn.PATH,
                example = "1"
            )
            @PathVariable("carId") Long carId,
            @Valid @RequestBody CarRegistrationRequest request,
            HttpSession session) {
        Long sellerId = (Long) session.getAttribute("memberId");
        if (sellerId == null) {
            return ResponseEntity.status(401).build();
        }
        carService.updateCar(carId, sellerId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "차량 삭제", description = "차량 ID로 차량을 삭제합니다.")
    @DeleteMapping("/cars/{carId}")
    public ResponseEntity<Void> deleteCar(
            @Parameter(
                name = "carId",
                description = "차량 ID", 
                required = true,
                in = ParameterIn.PATH,
                example = "1"
            )
            @PathVariable("carId") Long carId,
            HttpSession session) {
        Long sellerId = (Long) session.getAttribute("memberId");
        if (sellerId == null) {
            return ResponseEntity.status(401).build();
        }
        carService.deleteCar(carId, sellerId);
        return ResponseEntity.ok().build();
    }
}