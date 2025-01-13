package com.hsu.simcar.controller;

import com.hsu.simcar.domain.Member;
import com.hsu.simcar.dto.CarResponse;
import com.hsu.simcar.dto.MemberJoinRequest;
import com.hsu.simcar.dto.MemberLoginRequest;
import com.hsu.simcar.dto.MemberProfileResponse;
import com.hsu.simcar.dto.MemberUpdateRequest;
import com.hsu.simcar.service.CarService;
import com.hsu.simcar.service.MemberService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")  // /api에서 /api/members로 변경
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final CarService carService;

    @PostMapping("/join")
    public ResponseEntity<Void> join(@Valid @RequestBody MemberJoinRequest request) {
        memberService.join(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login") 
    public ResponseEntity<Void> login(@Valid @RequestBody MemberLoginRequest request, 
                                    HttpSession session) {
        Member member = memberService.login(request);
        session.setAttribute("memberId", member.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/profile")
    public ResponseEntity<MemberProfileResponse> getProfile(HttpSession session) {
        Long memberId = (Long) session.getAttribute("memberId");
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(memberService.getProfile(memberId));
    }

    @PutMapping("/profile")
    public ResponseEntity<Void> updateProfile(
            @Valid @RequestBody MemberUpdateRequest request,
            HttpSession session) {
        Long memberId = (Long) session.getAttribute("memberId");
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        memberService.updateProfile(memberId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/profile")
    public ResponseEntity<Void> deleteMember(HttpSession session) {
        Long memberId = (Long) session.getAttribute("memberId");
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        memberService.deleteMember(memberId);
        session.invalidate();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/sales")
    public ResponseEntity<List<CarResponse>> getMySales(HttpSession session) {
        Long sellerId = (Long) session.getAttribute("memberId");
        if (sellerId == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(carService.getMySales(sellerId));
    }
}
