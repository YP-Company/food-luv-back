package com.youngpotato.foodluv.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * DelegatingPasswordEncoder는 여러 암호화 알고리즘을 지원하며,
 * Spring Security의 기본 권장 알고리즘을 사용하여 비밀번호를 인코딩
 */

@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
