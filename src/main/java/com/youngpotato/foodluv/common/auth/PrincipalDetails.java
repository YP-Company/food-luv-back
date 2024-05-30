package com.youngpotato.foodluv.common.auth;

import com.youngpotato.foodluv.domain.member.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * security가 "/login" 주소 요청이 오면 낚아채서 로그인을 진행시킨다.
 * 로그인을 진행이 완료가 되면 security session을 만들어줌. (Security ContextHolder)
 * 오브젝트 -> Authentication 타입 객체
 * Authentication 안에 User 정보가 있어야 함.
 * User 오브젝트 타입 -> UserDetails 타입 객체
 *
 * Security Session -> Authentication -> UserDetails = PrincipalDetails
 */

public class PrincipalDetails implements UserDetails {

    private final Member member;

    private Map<String, Object> attributes;

    /** 일반 로그인 생성자 */
    public PrincipalDetails(Member member) {
        this.member = member;
    }

    /** OAuth 로그인 생성자 */
//    public PrincipalDetails(Member member, Map<String, Object> attributes) {
//        this.member = member;
//        this.attributes = attributes;
//    }

    /** memberId 리턴 */
    public Long getMemberId() {
        return member.getMemberId();
    }

    /** roles 리턴 */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        member.getRoleList().forEach(r -> authorities.add(() -> r));
        return authorities;
    }

    /** password 리턴 */
    @Override
    public String getPassword() {
        return member.getPassword();
    }

    /** email 리턴 */
    @Override
    public String getUsername() {
        return member.getEmail();
    }
}
