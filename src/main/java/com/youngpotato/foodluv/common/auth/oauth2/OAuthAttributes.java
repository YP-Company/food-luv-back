package com.youngpotato.foodluv.common.auth.oauth2;

import com.youngpotato.foodluv.common.auth.oauth2.provider.GoogleOAuth2UserInfo;
import com.youngpotato.foodluv.common.auth.oauth2.provider.KakaoOAuth2UserInfo;
import com.youngpotato.foodluv.common.auth.oauth2.provider.NaverOAuth2UserInfo;
import com.youngpotato.foodluv.common.auth.oauth2.provider.OAuth2UserInfo;
import lombok.Builder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import java.util.Map;

/**
 * 각 소셜에서 받아오는 데이터가 다르므로
 * 소셜별로 데이터를 받는 데이터를 분기 처리하는 DTO 클래스
 *
 * @param nameAttributeKey OAuth2 로그인 진행 시 키가 되는 필드 값, PK와 같은 의미
 * @param oauth2UserInfo   소셜 타입별 로그인 유저 정보(닉네임, 이메일, 프로필 사진 등등)
 */

@Builder
public record OAuthAttributes(
        String nameAttributeKey,
        OAuth2UserInfo oauth2UserInfo
) {

    /**
     * registrationId 에 맞는 메소드 호출하여 OAuthAttributes 객체 반환
     * 파라미터 : userNameAttributeName -> OAuth2 로그인 시 키(PK)가 되는 값 / attributes : OAuth 서비스의 유저 정보들
     * 소셜별 of 메소드(ofGoogle, ofKaKao, ofNaver)들은 각각 소셜 로그인 API에서 제공하는
     * 회원의 식별값(id), attributes, nameAttributeKey를 저장 후 build
     */
    public static OAuthAttributes of(String registrationId,
                                     String userNameAttributeName,
                                     Map<String, Object> attributes) {
        return switch (registrationId) {
            case "google" -> ofGoogle(userNameAttributeName, attributes);
            case "naver" -> ofNaver(attributes);
//            case "kakao" -> ofKakao(userNameAttributeName, attributes);
            default -> throw new OAuth2AuthenticationException("Sorry! Login with " + registrationId + " is not supported yet.");
        };
    }

    public static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new GoogleOAuth2UserInfo(attributes))
                .build();
    }

    private static OAuthAttributes ofNaver(Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey("id")
                .oauth2UserInfo(new NaverOAuth2UserInfo(attributes))
                .build();
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new KakaoOAuth2UserInfo(attributes))
                .build();
    }
}
