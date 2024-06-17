package com.youngpotato.foodluv.common.auth.oauth2.provider;

import java.util.Map;

/**
 * 소셜 로그인 사용자 정보를 추상화한 클래스
 */

public abstract class OAuth2UserInfo {

    protected Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }


    public abstract String getProvider();

    public abstract String getProviderId();

    public abstract String getEmail();

    public abstract String getNickname();
}
