package com.youngpotato.foodluv.web.dto;

import lombok.Builder;

@Builder
public record JwtDTO(
        String grantType,
        String accessToken,
        String refreshToken,
        Long refreshTokenExpirationTime
) {
}
