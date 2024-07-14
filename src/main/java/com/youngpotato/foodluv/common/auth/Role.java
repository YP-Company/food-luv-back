package com.youngpotato.foodluv.common.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    ROLE_ANONYMOUS("ROLE_ANONYMOUS"),   // 미인증 사용자
    ROLE_USER("ROLE_USER"),             // 일반 사용자
    ROLE_MANAGER("ROLE_MANAGER"),       // 매니저
    ROLE_ADMIN("ROLE_ADMIN");           // 관리자

    private final String role;
}
