package com.hsu.simcar.controller;

import com.hsu.simcar.dto.CarDetailResponse;
import com.hsu.simcar.dto.CarDiagnosisResponse;
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

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Car", description = "차량 관리 API")
@RequiredArgsConstructor
public class CarController {
    private final CarService carService;

    @Operation(summary = "차량 등록")
    @PostMapping(value = "/cars", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> registerCar(
        @RequestPart(value = "request") @Valid CarRegistrationRequest request,
        @RequestPart(value = "images", required = true) List<MultipartFile> images,
        HttpSession session) {
        
        Long sellerId = (Long) session.getAttribute("memberId");
        if (sellerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        if (images.isEmpty()) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("최소 1개 이상의 차량 이미지를 등록해야 합니다.");
        }
        
        carService.registerCar(sellerId, request, images);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "전체 차량 조회")
    @GetMapping("/cars")
    public ResponseEntity<List<CarResponse>> getAllCars() {
        return ResponseEntity.ok(carService.getAllCars());
    }

    @Operation(summary = "차량 상세 조회")
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

    @Operation(summary = "차량 정보 수정")
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

    @Operation(summary = "차량 대표 이미지 변경")
    @PutMapping("/cars/{carId}/thumbnail/{imageId}")
    public ResponseEntity<Void> updateThumbnail(
        @Parameter(description = "차량 ID", required = true) @PathVariable Long carId,
        @Parameter(description = "이미지 ID", required = true) @PathVariable Long imageId,
        HttpSession session) {
        
        Long sellerId = (Long) session.getAttribute("memberId");
        if (sellerId == null) {
            return ResponseEntity.status(401).build();
        }
        carService.updateThumbnail(carId, imageId, sellerId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "차량 삭제")
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

    @Operation(summary = "차량 신뢰도 진단")
    @GetMapping("/cars/{carId}/diagnosis")
    public ResponseEntity<CarDiagnosisResponse> diagnoseCar(
        @Parameter(
            name = "carId",
            description = "차량 ID", 
            required = true,
            in = ParameterIn.PATH,
            example = "1"
        )
        @PathVariable("carId") Long carId) {
        return ResponseEntity.ok(carService.diagnoseCar(carId));
    }
}