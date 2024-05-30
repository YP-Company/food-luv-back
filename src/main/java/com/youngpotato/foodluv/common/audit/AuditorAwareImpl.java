package com.youngpotato.foodluv.common.audit;

import com.youngpotato.foodluv.common.auth.PrincipalDetails;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.stream.Collectors;

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
     * Authentication 객체가 인증되지 않았거나 익명 사용자인지 확인
     */
    public boolean isAnonymous(Authentication authentication) {
        return authentication == null || authentication.getName() == null ||
                (authentication.getPrincipal().equals("anonymousUser") &&
                        authentication.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.joining(","))
                                .equals("ROLE_ANONYMOUS")
                );
    }
}
