package com.youngpotato.foodluv.web.dto;

import com.youngpotato.foodluv.common.auth.Role;
import com.youngpotato.foodluv.domain.member.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignUpDTO(
        @Email(message = "이메일 형식이 아닙니다.")
        @NotBlank(message="이메일을 입력해주세요.")
        String email,

        @NotBlank(message = "비밀번호를 입력해주세요.")
        String password,

        @NotBlank(message = "닉네임을 입력해주세요.")
        String nickname
) {
    public Member toEntity(String encodedPassword, Role role) {
        return Member.builder()
                .email(this.email)
                .password(encodedPassword)
                .nickname(this.nickname)
                .role(role)
                .build();
    }
}
