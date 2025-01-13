package com.hsu.simcar.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hsu.simcar.dto.CarResponse;
import com.hsu.simcar.service.FavoriteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "Favorite", description = "찜하기 API")
public class FavoriteController {
    private final FavoriteService favoriteService;
    
    @Operation(summary = "찜하기", description = "차량을 찜합니다.")
    @PostMapping("/api/favorites/{carId}")
    public ResponseEntity<Void> addFavorite(
        @Parameter(
            name = "carId",
            description = "차량 ID", 
            required = true,
            in = ParameterIn.PATH,
            example = "1"
        )
        @PathVariable("carId") Long carId,
        HttpSession session) {
        Long memberId = (Long) session.getAttribute("memberId");
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        favoriteService.addFavorite(memberId, carId);
        return ResponseEntity.ok().build();
    }
    
    @Operation(summary = "찜하기 취소", description = "차량 찜하기를 취소합니다.")
    @DeleteMapping("/api/favorites/{carId}")
    public ResponseEntity<Void> removeFavorite(
        @Parameter(
            name = "carId",
            description = "차량 ID", 
            required = true,
            in = ParameterIn.PATH,
            example = "1"
        )
        @PathVariable("carId") Long carId,
        HttpSession session) {
        Long memberId = (Long) session.getAttribute("memberId");
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        favoriteService.removeFavorite(memberId, carId);
        return ResponseEntity.ok().build();
    }
    
    @Operation(summary = "찜한 차량 조회", description = "찜한 차량 목록을 조회합니다.")
    @GetMapping("/api/members/favorites")
    public ResponseEntity<List<CarResponse>> getFavorites(HttpSession session) {
        Long memberId = (Long) session.getAttribute("memberId");
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(favoriteService.getFavorites(memberId));
    }
}