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

        // Authentication 객체가 인증되지 않았거나 익명 사용자인 경우
        if (authentication == null || authentication.getName() == null || authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.of(0L);
        }

        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return Optional.of(principal.getMember().getMemberId());
    }
}
