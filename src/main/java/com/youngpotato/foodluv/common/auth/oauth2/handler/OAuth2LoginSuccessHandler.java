package com.youngpotato.foodluv.common.auth.oauth2.handler;

import com.youngpotato.foodluv.common.jwt.JwtProvider;
import com.youngpotato.foodluv.web.dto.JwtDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 소셜 로그인 성공시 jwt 생성하여 헤더에 주입
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        log.info("OAuth2 로그인 성공");

        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        JwtDTO jwtDTO = jwtProvider.generateToken(authentication);

        redisTemplate.opsForValue().set(
                "RT:" + oAuth2User.getAttributes().get("email"),
                jwtDTO.refreshToken(),
                jwtDTO.refreshTokenExpirationTime(),
                TimeUnit.MICROSECONDS
        );

        // 아래 ??
//        String targetUrl = UriComponentsBuilder
//                .fromUriString("/api/v1/success-oauth")
//                .queryParam("token", jwtDTO.accessToken())
//                .build()
//                .toUriString();
//
//        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
