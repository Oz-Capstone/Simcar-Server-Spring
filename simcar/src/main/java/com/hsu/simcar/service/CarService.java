package com.hsu.simcar.service;

import com.hsu.simcar.domain.Car;
import com.hsu.simcar.domain.Member;
import com.hsu.simcar.dto.CarRegistrationRequest;
import com.hsu.simcar.dto.CarResponse;
import com.hsu.simcar.dto.CarDetailResponse;
import com.hsu.simcar.dto.CarDiagnosisResponse;
import com.hsu.simcar.repository.CarRepository;
import com.hsu.simcar.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CarService {
    private final CarRepository carRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void registerCar(Long sellerId, CarRegistrationRequest request) {
        Member seller = memberRepository.findById(sellerId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다"));

        Car car = Car.builder()
                .seller(seller)
                .type(request.getType())
                .price(request.getPrice())
                .brand(request.getBrand())
                .model(request.getModel())
                .productionYear(request.getYear())
                .mileage(request.getMileage())
                .fuelType(request.getFuelType())
                .imageUrl(request.getImageUrl())
                .carNumber(request.getCarNumber())
                .insuranceHistory(request.getInsuranceHistory())
                .inspectionHistory(request.getInspectionHistory())
                .color(request.getColor())
                .transmission(request.getTransmission())
                .region(request.getRegion())
                .contactNumber(request.getContactNumber())
                .build();

        carRepository.save(car);
    }

    @Transactional(readOnly = true)
    public List<CarResponse> getAllCars() {
        return carRepository.findAll().stream()
                .map(CarResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CarDetailResponse getCarDetail(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 차량입니다"));
        return CarDetailResponse.from(car);
    }

    @Transactional(readOnly = true)
    public List<CarResponse> getMySales(Long sellerId) {
        Member seller = memberRepository.findById(sellerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다"));
        return carRepository.findAllBySeller(seller).stream()
                .map(CarResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateCar(Long carId, Long sellerId, CarRegistrationRequest request) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 차량입니다"));

        if (!car.getSeller().getId().equals(sellerId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다");
        }

        // Car 엔티티에 업데이트 메서드 추가 필요
        car.update(request);
        carRepository.save(car);
    }

    @Transactional
    public void deleteCar(Long carId, Long sellerId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 차량입니다"));

        if (!car.getSeller().getId().equals(sellerId)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다");
        }

        carRepository.delete(car);
    }

    @Transactional(readOnly = true)
    public CarDiagnosisResponse diagnoseCar(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 차량입니다"));
                
        // TODO: AI 서비스 연동 후 실제 진단 로직 구현
        // 현재는 임시 응답을 반환
        return CarDiagnosisResponse.builder()
                .carId(car.getId())
                .reliabilityScore(85) // 임시 점수
                .evaluationComment("AI 진단 서비스 준비 중입니다.")
                .build();
    }
}