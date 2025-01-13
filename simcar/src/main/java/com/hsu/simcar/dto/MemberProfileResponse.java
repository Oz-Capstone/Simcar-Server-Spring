package com.hsu.simcar.dto;

import com.hsu.simcar.domain.Member;
import lombok.Data;

@Data
public class MemberProfileResponse {
    private String email;
    private String name;
    private String phone;

    public static MemberProfileResponse from(Member member) {
        MemberProfileResponse response = new MemberProfileResponse();
        response.setEmail(member.getEmail());
        response.setName(member.getName());
        response.setPhone(member.getPhone());
        return response;
    }
}