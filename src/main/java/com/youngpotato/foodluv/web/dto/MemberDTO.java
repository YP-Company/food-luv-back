package com.youngpotato.foodluv.web.dto;

import com.youngpotato.foodluv.domain.member.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberDTO {

    private String email;

    private String nickname;

    public static MemberDTO from(Member member) {
        return MemberDTO.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .build();
    }
}
