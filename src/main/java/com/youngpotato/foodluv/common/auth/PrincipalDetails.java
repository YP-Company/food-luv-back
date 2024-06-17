package com.youngpotato.foodluv.common.auth;

import com.youngpotato.foodluv.domain.member.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

/**
 * security가 "/login" 주소 요청이 오면 낚아채서 로그인을 진행시킨다.
 * 로그인을 진행이 완료가 되면 security session을 만들어줌. (Security ContextHolder)
 * 오브젝트 -> Authentication 타입 객체
 * Authentication 안에 User 정보가 있어야 함.
 * User 오브젝트 타입 -> UserDetails 타입 객체
 *
 * Security Session -> Authentication -> UserDetails = PrincipalDetails
 */

@Getter
public class PrincipalDetails implements UserDetails, OAuth2User {

    private final Member member;

    private Map<String, Object> attributes;

    /** 일반 로그인 생성자 */
    public PrincipalDetails(Member member) {
        this.member = member;
    }

    /** OAuth 로그인 생성자 */
    public PrincipalDetails(Member member, Map<String, Object> attributes) {
        this.member = member;
        this.attributes = attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 역할 목록
        GrantedAuthority roleAuthority = new SimpleGrantedAuthority(Role.ROLE_USER.getRole());

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(roleAuthority);

        return authorities;
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getEmail();
    }

    // 하위 메서드 OAuth2User 구현
    @Override
    public String getName() {
        // OAuth2 인증에서는 사용되지 않는 메서드이므로 null 반환
        return null;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
