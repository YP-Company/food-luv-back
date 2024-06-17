package com.youngpotato.foodluv.config;

import com.youngpotato.foodluv.common.auth.oauth2.handler.OAuth2LoginFailureHandler;
import com.youngpotato.foodluv.common.auth.oauth2.handler.OAuth2LoginSuccessHandler;
import com.youngpotato.foodluv.common.auth.oauth2.service.PrincipalOauth2UserService;
import com.youngpotato.foodluv.common.jwt.JwtAuthenticationFilter;
import com.youngpotato.foodluv.common.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
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
    private final PrincipalOauth2UserService principalOauth2UserService;

    /**
     * security를 적용하지 않을 리소스
     * error endpoint를 열어줘야 함, favicon.ico 추가!
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers("/error", "/favicon.ico");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable) // cookie를 사용하지 않으면 꺼도 된다. (cookie를 사용할 경우 httpOnly(XSS 방어), sameSite(CSRF 방어)로 방어해야 한다.)
                .addFilter(corsConfig.corsFilter())
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(AbstractHttpConfigurer::disable) // 기본 인증 로그인 비활성화
                .formLogin(AbstractHttpConfigurer::disable) // 기본 login form 비활성화
                .logout(AbstractHttpConfigurer::disable) // 기본 logout 비활성화
                .headers(c ->c.frameOptions(
                        FrameOptionsConfig::disable).disable()) // X-Frame-Options 비활성화
                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 사용하지 않음
                )

                // request 인증, 인가 설정
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(new AntPathRequestMatcher("/api/v1/user/**")).authenticated()
                                .requestMatchers(new AntPathRequestMatcher("/api/v1/manager/**")).hasAnyRole("MANAGER", "ADMIN")
                                .requestMatchers(new AntPathRequestMatcher("/api/v1/admin/**")).hasAnyRole("ADMIN")
                                .requestMatchers(
                                        new AntPathRequestMatcher("/api/v1/sign-up"), // 회원가입
                                        new AntPathRequestMatcher("/api/v1/sign-in"), // 로그인
                                        // 아이콘, css, js 관련
                                        new AntPathRequestMatcher("/"),
                                        new AntPathRequestMatcher("/css/**"),
                                        new AntPathRequestMatcher("/images/**"),
                                        new AntPathRequestMatcher("/js/**"),
                                        new AntPathRequestMatcher("/h2-console/**") // H2 DB
                                ).permitAll()
                                .anyRequest().permitAll()
                )

                // oauth2 설정
                .oauth2Login(oauth -> // OAuth2 로그인 기능에 대한 여러 설정의 진입점
                        // OAuth2 로그인 성공 이후 사용자 정보를 가져올 때의 설정을 담당
                        oauth.userInfoEndpoint(c -> c.userService(principalOauth2UserService))
                                .successHandler(oAuth2LoginSuccessHandler()) // 로그인 성공 시 핸들러
                                .failureHandler(oAuth2LoginFailureHandler()) // 로그인 실패 시 핸들러
                )

                // JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 앞에 추가하여 JWT 인증을 처리
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtProvider, redisTemplate),
                        UsernamePasswordAuthenticationFilter.class
                );

        return httpSecurity.build();
    }

    @Bean
    public AuthenticationSuccessHandler oAuth2LoginSuccessHandler() {
        return new OAuth2LoginSuccessHandler(jwtProvider, redisTemplate);
    }

    @Bean
    public AuthenticationFailureHandler oAuth2LoginFailureHandler() {
        return new OAuth2LoginFailureHandler();
    }
}
