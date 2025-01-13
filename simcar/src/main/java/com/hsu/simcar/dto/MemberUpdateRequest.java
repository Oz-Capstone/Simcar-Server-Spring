package com.hsu.simcar.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class MemberUpdateRequest {
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", 
            message = "비밀번호는 8자 이상, 문자와 숫자를 포함해야 합니다")
    @Schema(description = "새 비밀번호 (선택)", example = "newpassword123")
    private String password;

    @NotBlank(message = "이름은 필수입니다")
    @Schema(description = "사용자 이름", example = "홍길동")
    private String name;

    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", 
            message = "전화번호 형식이 올바르지 않습니다")
    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phone;
}