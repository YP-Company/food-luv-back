package com.youngpotato.foodluv.common.audit;

import com.youngpotato.foodluv.common.auth.PrincipalDetails;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (isAnonymous(authentication)) {
            return Optional.of(0L);
        }

        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return Optional.of(principal.getMemberId());
    }

    /**
     * Authentication 객체가 인증되지 않았거나 익명 사용자라면 true 반환
     */
    public boolean isAnonymous(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return true;
        }

        return "anonymousUser".equals(authentication.getPrincipal()) &&
                authentication.getAuthorities().stream().anyMatch(auth -> "ROLE_ANONYMOUS".equals(auth.getAuthority()));
    }
}
