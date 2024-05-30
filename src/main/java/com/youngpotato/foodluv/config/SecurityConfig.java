package com.youngpotato.foodluv.config;

import com.youngpotato.foodluv.common.jwt.JwtAuthenticationFilter;
import com.youngpotato.foodluv.common.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Spring Security의 설정을 담당
 */

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // REST API이므로 csrf 보안 및 basic auth를 사용하지 않음
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)

                // JWT를 사용하기 때문에 세션을 사용하지 않음
                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 요청에 대한 인가 규칙 설정
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/sign-up")).permitAll()
                                .requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/sign-in")).permitAll()
                                .requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/user/**")).authenticated()
                                .requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/manager/**")).hasAnyRole("MANAGER", "ADMIN")
                                .requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/admin/**")).hasAnyRole("ADMIN")
                                .anyRequest().permitAll()
                )

                // JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 앞에 추가하여 JWT 인증을 처리
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtProvider),
                        UsernamePasswordAuthenticationFilter.class
                )

                // H2 DB 활성화를 위해 설정
                .headers(
                        headersConfigurer ->
                                headersConfigurer
                                        .frameOptions(
                                                HeadersConfigurer.FrameOptionsConfig::sameOrigin
                                        )
                );

        return httpSecurity.build();
    }

    /**
     * DelegatingPasswordEncoder는 여러 암호화 알고리즘을 지원하며,
     * Spring Security의 기본 권장 알고리즘을 사용하여 비밀번호를 인코딩
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
