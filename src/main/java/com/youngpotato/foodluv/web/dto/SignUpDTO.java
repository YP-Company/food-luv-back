package com.youngpotato.foodluv.web.dto;

import com.youngpotato.foodluv.domain.member.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SignUpDTO {

    @Email(message = "이메일 형식이 아닙니다.")
    @NotBlank(message="이메일을 입력해주세요.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    @NotBlank(message = "닉네임을 입력해주세요.")
    private String nickname;

    public Member toEntity(String encodedPassword, String roles) {
        return Member.builder()
                .email(this.email)
                .password(encodedPassword)
                .nickname(this.nickname)
                .roles(roles)
                .build();
    }
}
