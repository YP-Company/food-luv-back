package com.youngpotato.foodluv.config;

import com.youngpotato.foodluv.common.jwt.JwtAuthenticationFilter;
import com.youngpotato.foodluv.common.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.CorsFilter;

/**
 * Spring Security의 설정을 담당
 */

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsConfig corsConfig;
    private final CorsFilter corsFilter;
    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // REST API이므로 csrf 보안 및 basic auth를 사용하지 않음
                .csrf(AbstractHttpConfigurer::disable)
                .addFilter(corsConfig.corsFilter())
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
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
                        new JwtAuthenticationFilter(jwtProvider, redisTemplate),
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
}
