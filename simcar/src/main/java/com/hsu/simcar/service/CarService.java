package com.hsu.simcar.service;

import com.hsu.simcar.domain.Car;
import com.hsu.simcar.domain.CarImage;
import com.hsu.simcar.domain.Member;
import com.hsu.simcar.dto.CarRegistrationRequest;
import com.hsu.simcar.dto.CarResponse;
import com.hsu.simcar.dto.CarDetailResponse;
import com.hsu.simcar.dto.CarDiagnosisResponse;
import com.hsu.simcar.repository.CarRepository;
import com.hsu.simcar.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CarService {
    @Value("${app.server.url}")
    private String appServerUrl;
    private final CarRepository carRepository;
    private final MemberRepository memberRepository;
    private final AICarDiagnosisService aiCarDiagnosisService;
    private final FileService fileService;

    @Transactional
    public void registerCar(Long sellerId, CarRegistrationRequest request, List<MultipartFile> images) {
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
                .carNumber(request.getCarNumber())
                .insuranceHistory(request.getInsuranceHistory())
                .inspectionHistory(request.getInspectionHistory())
                .color(request.getColor())
                .transmission(request.getTransmission())
                .region(request.getRegion())
                .contactNumber(request.getContactNumber())
                .build();

        carRepository.save(car);

        for (int i = 0; i < images.size(); i++) {
            try {
                MultipartFile image = images.get(i);
                String storedFileName = fileService.saveFile(image);
                
                CarImage carImage = CarImage.builder()
                    .car(car)
                    .originalFileName(image.getOriginalFilename())
                    .storedFileName(storedFileName)
                    .filePath(appServerUrl + "/uploads/" + storedFileName) // URL 전체 경로 사용
                    .fileSize(image.getSize())
                    .contentType(image.getContentType())
                    .isThumbnail(i == 0) // 첫 번째 이미지를 대표 이미지로 설정
                    .build();
                    
                car.addImage(carImage);
            } catch (IOException e) {
                throw new RuntimeException("파일 저장 실패: " + e.getMessage());
            }
        }
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
    public void updateThumbnail(Long carId, Long imageId, Long sellerId) {
        Car car = carRepository.findById(carId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 차량입니다"));

        if (!car.getSeller().getId().equals(sellerId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다");
        }

        // 모든 이미지의 썸네일 상태를 false로 변경
        car.getImages().forEach(image -> image.setThumbnail(false));

        // 선택한 이미지를 썸네일로 설정
        CarImage newThumbnail = car.getImages().stream()
            .filter(image -> image.getId().equals(imageId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이미지입니다"));
        
        newThumbnail.setThumbnail(true);
    }

    @Transactional
    public void deleteCar(Long carId, Long sellerId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 차량입니다"));

        if (!car.getSeller().getId().equals(sellerId)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다");
        }

        for (CarImage image : car.getImages()) {
            try {
                fileService.deleteFile(image.getStoredFileName());
            } catch (IOException e) {
                throw new RuntimeException("파일 삭제 실패: " + e.getMessage());
            }
        }

        carRepository.delete(car);
    }

    @Transactional(readOnly = true)
    public CarDiagnosisResponse diagnoseCar(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 차량입니다"));

        return aiCarDiagnosisService.diagnose(car);
    }

    @Transactional
    public void addImages(Long carId, Long sellerId, List<MultipartFile> images) {
        Car car = carRepository.findById(carId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 차량입니다"));
            
        if (!car.getSeller().getId().equals(sellerId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다");
        }

        for (MultipartFile image : images) {
            try {
                String storedFileName = fileService.saveFile(image);
                
                CarImage carImage = CarImage.builder()
                    .car(car)
                    .originalFileName(image.getOriginalFilename())
                    .storedFileName(storedFileName)
                    .filePath(appServerUrl + "/uploads/" + storedFileName) // URL 전체 경로 사용 
                    .fileSize(image.getSize())
                    .contentType(image.getContentType())
                    .isThumbnail(false)
                    .build();
                    
                car.addImage(carImage);
            } catch (IOException e) {
                throw new RuntimeException("파일 저장 실패: " + e.getMessage());
            }
        }
    }

    @Transactional
    public void deleteImage(Long carId, Long imageId, Long sellerId) {
        Car car = carRepository.findById(carId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 차량입니다"));
            
        if (!car.getSeller().getId().equals(sellerId)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다");
        }

        CarImage targetImage = car.getImages().stream()
            .filter(img -> img.getId().equals(imageId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이미지입니다"));

        boolean wasDeletedImageThumbnail = targetImage.isThumbnail();

        try {
            fileService.deleteFile(targetImage.getStoredFileName());
            car.removeImage(targetImage);

            // 삭제된 이미지가 대표 이미지였고 남은 이미지가 있다면
            if (wasDeletedImageThumbnail && !car.getImages().isEmpty()) {
                // 첫 번째 이미지를 대표 이미지로 설정
                car.getImages().get(0).setThumbnail(true);
            }
        } catch (IOException e) {
            throw new RuntimeException("파일 삭제 실패: " + e.getMessage());
        }
    }

    @Transactional
    public void updateImagesOrder(Long carId, List<Long> imageIds, Long sellerId) {
        Car car = carRepository.findById(carId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 차량입니다"));
            
        if (!car.getSeller().getId().equals(sellerId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다");
        }

        car.updateImagesOrder(imageIds);
    }
}