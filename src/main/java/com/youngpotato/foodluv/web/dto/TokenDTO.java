package com.youngpotato.foodluv.web.dto;

public record TokenDTO(
        String accessToken,
        String refreshToken
) {
}
