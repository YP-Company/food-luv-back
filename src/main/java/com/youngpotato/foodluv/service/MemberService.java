package com.youngpotato.foodluv.service;

import com.youngpotato.foodluv.common.jwt.JwtProvider;
import com.youngpotato.foodluv.domain.member.Member;
import com.youngpotato.foodluv.domain.member.MemberRepository;
import com.youngpotato.foodluv.web.dto.JwtDTO;
import com.youngpotato.foodluv.web.dto.MemberDTO;
import com.youngpotato.foodluv.web.dto.SignInDTO;
import com.youngpotato.foodluv.web.dto.SignUpDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtProvider jwtProvider;

    @Transactional
    public MemberDTO signup(SignUpDTO signUpDTO) {
        // 이메일, 닉네임 중복 체크
        if (memberRepository.existsByEmail(signUpDTO.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        if (memberRepository.existsByNickname(signUpDTO.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(signUpDTO.getPassword());

        // 회원가입 처리
        Member newMember = memberRepository.save(signUpDTO.toEntity(encodedPassword, "ROLE_USER"));

        return MemberDTO.toDto(newMember);
    }

    @Transactional
    public JwtDTO signIn(SignInDTO signInDTO) {
        // 1. username + password를 기반으로 Authentication 객체 생성
        // 이때 authentication은 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(signInDTO.email(), signInDTO.password());

        // 2. 실제 검증.
        // authenticate() 메서드를 통해 요청된 Member에 대한 검증 진행
        // authenticate 메서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        return jwtProvider.generateToken(authentication);
    }
}
