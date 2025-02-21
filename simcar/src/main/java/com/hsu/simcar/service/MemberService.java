package com.hsu.simcar.service;

import lombok.extern.slf4j.Slf4j;
import com.hsu.simcar.domain.Car;
import com.hsu.simcar.domain.Member;
import com.hsu.simcar.dto.MemberJoinRequest;
import com.hsu.simcar.dto.MemberLoginRequest;
import com.hsu.simcar.dto.MemberProfileResponse;
import com.hsu.simcar.dto.MemberUpdateRequest;
import com.hsu.simcar.repository.CarRepository;
import com.hsu.simcar.repository.FavoriteRepository;
import com.hsu.simcar.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final FavoriteRepository favoriteRepository;
    private final FileService fileService;
    private final CarRepository carRepository;

    @Transactional
    public void join(MemberJoinRequest request) {
        // 이메일 중복 체크
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        Member member = Member.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // 비밀번호 암호화
                .name(request.getName())
                .phone(request.getPhone())
                .build();

        memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public Member login(MemberLoginRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return member;
    }
    
    @Transactional(readOnly = true)
    public MemberProfileResponse getProfile(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        return MemberProfileResponse.from(member);
    }

    @Transactional
    public void updateProfile(Long memberId, MemberUpdateRequest request) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        
        member.updateProfile(
            request.getPassword() != null ? passwordEncoder.encode(request.getPassword()) : member.getPassword(),
            request.getName(),
            request.getPhone()
        );
    }

    @Transactional
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 1. 회원이 찜한 목록 삭제
        favoriteRepository.deleteAllByMember(member);

        // 2. 다른 사용자가 이 회원의 차량을 찜한 목록 삭제
        favoriteRepository.deleteAllByCarIn(carRepository.findAllBySeller(member));

        // 3. 회원이 등록한 차량 찾기 및 처리
        List<Car> memberCars = carRepository.findAllBySeller(member);
        memberCars.forEach(car -> {
            car.getImages().forEach(image -> {
                try {
                    if (fileService.exists(image.getStoredFileName())) {
                        fileService.deleteFile(image.getStoredFileName());
                    }
                } catch (IOException e) {
                    log.warn("파일 삭제 실패: {}", image.getStoredFileName());
                    // 파일 삭제 실패해도 계속 진행
                }
            });
            carRepository.delete(car);
        });

        // 4. 회원 정보 삭제
        memberRepository.delete(member);
    }
}
