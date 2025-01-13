package com.hsu.simcar.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hsu.simcar.domain.Car;
import com.hsu.simcar.domain.Favorite;
import com.hsu.simcar.domain.Member;
import com.hsu.simcar.dto.CarResponse;
import com.hsu.simcar.repository.CarRepository;
import com.hsu.simcar.repository.FavoriteRepository;
import com.hsu.simcar.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final MemberRepository memberRepository;
    private final CarRepository carRepository;
    
    @Transactional
    public void addFavorite(Long memberId, Long carId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다"));
        Car car = carRepository.findById(carId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 차량입니다"));
            
        if (favoriteRepository.existsByMemberAndCar(member, car)) {
            throw new IllegalArgumentException("이미 찜한 차량입니다");
        }
        
        favoriteRepository.save(Favorite.builder()
            .member(member)
            .car(car)
            .build());
    }
    
    @Transactional
    public void removeFavorite(Long memberId, Long carId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다"));
        Car car = carRepository.findById(carId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 차량입니다"));
            
        Favorite favorite = favoriteRepository.findByMemberAndCar(member, car)
            .orElseThrow(() -> new IllegalArgumentException("찜하지 않은 차량입니다"));
            
        favoriteRepository.delete(favorite);
    }
    
    @Transactional(readOnly = true)
    public List<CarResponse> getFavorites(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다"));
            
        return favoriteRepository.findAllByMember(member).stream()
            .map(favorite -> CarResponse.from(favorite.getCar()))
            .collect(Collectors.toList());
    }
}