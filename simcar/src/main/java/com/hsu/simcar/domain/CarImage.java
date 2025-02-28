package com.hsu.simcar.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CarImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id")
    private Car car;

    @Column(name = "image_order")
    private Integer order;
    
    private String originalFileName;    // 원본 파일명
    private String storedFileName;      // 저장된 파일명
    private String filePath;            // 저장 경로
    private Long fileSize;              // 파일 크기
    private String contentType;         // 파일 타입
    private boolean isThumbnail;        // 대표 이미지 여부
    

    public void setCar(Car car) {
        this.car = car;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}
