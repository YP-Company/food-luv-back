package com.youngpotato.foodluv.web.dto;

import com.youngpotato.foodluv.domain.member.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberDTO {

    private Long memberId;

    private String email;

    private String nickname;

    public static MemberDTO toDto(Member member) {
        return MemberDTO.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .build();
    }
}
