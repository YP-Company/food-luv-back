package com.youngpotato.foodluv.service;

import com.youngpotato.foodluv.common.auth.Role;
import com.youngpotato.foodluv.common.jwt.JwtProvider;
import com.youngpotato.foodluv.domain.member.Member;
import com.youngpotato.foodluv.domain.member.MemberRepository;
import com.youngpotato.foodluv.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional
    public MemberDTO signup(SignUpDTO signUpDTO) {
        // 이메일, 닉네임 중복 체크
        if (memberRepository.existsByEmail(signUpDTO.email())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        if (memberRepository.existsByNickname(signUpDTO.nickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(signUpDTO.password());

        // 회원가입 처리
        Member newMember = memberRepository.save(signUpDTO.toEntity(encodedPassword, Role.ROLE_USER));

        return MemberDTO.toDto(newMember);
    }

    public JwtDTO signIn(SignInDTO signInDTO) {
        // 1. email, password를 기반으로 Authentication 객체 생성
        // authentication 에서 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(signInDTO.email(), signInDTO.password());

        // 2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
        // authenticate() 메서드를 통해 요청된 Member에 대한 검증 진행
        // authenticate 메서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        JwtDTO jwtDTO = jwtProvider.generateToken(authentication);

        // 4. RefreshToken Redis 저장 (expirationTime 설정을 통해 자동 삭제 처리)
        redisTemplate.opsForValue().set(
                "RT:" + authentication.getName(),
                jwtDTO.refreshToken(),
                jwtDTO.refreshTokenExpirationTime(),
                TimeUnit.MILLISECONDS
        );

        return jwtDTO;
    }

    public JwtDTO reissue(TokenDTO tokenDTO) {
        // 1. Refresh Token 검증
        if (!jwtProvider.validateToken(tokenDTO.refreshToken())) {
            throw new RuntimeException("Refresh Token 검증에 실패하였습니다.");
        }

        // 2. Access Token 에서 User email 을 가져옵니다.
        Authentication authentication = jwtProvider.getAuthentication(tokenDTO.accessToken());

        // 3. Redis 에서 User email 을 기반으로 저장된 Refresh Token 값을 가져옵니다.
        String refreshToken = (String) redisTemplate.opsForValue().get("RT:" + authentication.getName());
        // 로그아웃되어 Redis 에 RefreshToken 이 존재하지 않는 경우 처리
        if(ObjectUtils.isEmpty(refreshToken) || !refreshToken.equals(tokenDTO.refreshToken())) {
            throw new RuntimeException("로그아웃된 사용자입니다.");
        }

        // 4. 새로운 토큰 생성
        JwtDTO jwtDTO = jwtProvider.generateToken(authentication);

        // 5. RefreshToken Redis 업데이트
        redisTemplate.opsForValue().set(
                "RT:" + authentication.getName(),
                jwtDTO.refreshToken(),
                jwtDTO.refreshTokenExpirationTime(),
                TimeUnit.MILLISECONDS
        );

        return jwtDTO;
    }

    public void signOut(TokenDTO tokenDTO) {
        // 1. Access Token 검증
        if (!jwtProvider.validateToken(tokenDTO.accessToken())) {
            throw new RuntimeException("Access Token 검증에 실패하였습니다.");
        }

        // 2. Access Token 에서 User email 을 가져옵니다.
        Authentication authentication = jwtProvider.getAuthentication(tokenDTO.accessToken());

        // 3. Redis 에서 해당 User email 로 저장된 Refresh Token 이 있는지 여부를 확인 후 있을 경우 삭제
        if (redisTemplate.opsForValue().get("RT:" + authentication.getName()) != null) {
            redisTemplate.delete("RT:" + authentication.getName());
            redisTemplate.delete(authentication.getName());
        }

        // 4. 해당 Access Token 유효시간 가지고 와서 BlackList 로 저장하기
        Long expiration = jwtProvider.getExpiration(tokenDTO.accessToken());
        redisTemplate.opsForValue().set(
                tokenDTO.accessToken(), "logout", expiration, TimeUnit.MILLISECONDS
        );
    }
}
