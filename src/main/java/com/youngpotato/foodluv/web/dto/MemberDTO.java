package com.youngpotato.foodluv.web.dto;

import com.youngpotato.foodluv.domain.member.Member;
import lombok.Builder;

@Builder
public record MemberDTO(
        Long memberId,
        String email,
        String nickname
) {
    public static MemberDTO toDto(Member member) {
        return MemberDTO.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .build();
    }
}
