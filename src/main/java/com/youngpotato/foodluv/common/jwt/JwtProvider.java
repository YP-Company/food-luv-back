package com.youngpotato.foodluv.common.jwt;

import com.youngpotato.foodluv.common.Constants;
import com.youngpotato.foodluv.web.dto.JwtDTO;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtProvider {

    private final Key key;
    private final long accessTokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;

    public JwtProvider(@Value("${jwt.secret}") String secretKey,
                       @Value("${jwt.access-token-validity-in-seconds}") long accessTokenValidityInSeconds,
                       @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValidityInSeconds) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenValidityInMilliseconds = accessTokenValidityInSeconds;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInSeconds;
    }

    /**
     * 인증(Authentication) 객체를 기반으로 Access Token과 Refresh Token 생성
     */
    public JwtDTO generateToken(Authentication authentication) {
        // 권한 정보
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + accessTokenValidityInMilliseconds);
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(Constants.JWT_AUTHORITIES_KEY, authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + refreshTokenValidityInMilliseconds))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return JwtDTO.builder()
                .grantType(Constants.JWT_TOKEN_PREFIX)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * 주어진 Access token을 복호화하여 사용자의 인증 정보(Authentication 객체)를 생성
     */
    public Authentication getAuthentication(String accessToken) {
        // Jwt 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get(Constants.JWT_AUTHORITIES_KEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(Constants.JWT_AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication return
        // UserDetails: interface, User: UserDetails를 구현한 class
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    /**
     * 주어진 토큰을 검증하여 유효성을 확인
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token(잘못된 JWT 서명입니다.)", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token(만료된 JWT 토큰입니다.)", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token(지원되지 않는 JWT 토큰입니다.)", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.(JWT 토큰이 잘못되었습니다.)", e);
        }

        return false;
    }

    /**
     * 주어진 Access token을 복호화하고, 만료된 토큰인 경우에도 Claims 반환
     */
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
