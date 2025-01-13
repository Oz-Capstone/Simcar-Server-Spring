package com.hsu.simcar.service;

import com.hsu.simcar.domain.Member;
import com.hsu.simcar.dto.MemberJoinRequest;
import com.hsu.simcar.dto.MemberLoginRequest;
import com.hsu.simcar.dto.MemberProfileResponse;
import com.hsu.simcar.dto.MemberUpdateRequest;
import com.hsu.simcar.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

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
        if (!memberRepository.existsById(memberId)) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }
        memberRepository.deleteById(memberId);
    }
}
