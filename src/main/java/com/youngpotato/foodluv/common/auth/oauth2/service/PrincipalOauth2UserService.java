package com.youngpotato.foodluv.common.auth.oauth2.service;

import com.youngpotato.foodluv.common.auth.Role;
import com.youngpotato.foodluv.common.auth.oauth2.OAuthAttributes;
import com.youngpotato.foodluv.common.auth.oauth2.handler.OAuth2LoginSuccessHandler;
import com.youngpotato.foodluv.domain.member.Member;
import com.youngpotato.foodluv.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

/**
 * 정상적인 유저 인증이 완료되면 -> 여기로 오게됨 -> 그 다음에 {@link OAuth2LoginSuccessHandler} 감
 */
@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * OAuth2 로그인 처리 (자동 회원가입)
     * Provider(구글, 네이버 등)로 부터 받은 userRequest 데이터에 대한 후처리가 진행되는 메소드
     *
     * 소셜 로그인 클릭 > 소셜 로그인창 > 로그인 완료 > code 리턴 (OAuth-Client 라이브러리가 받음)
     * > AccessToken Request > userRequest 정보 > loadUser() 메소드 호출 > Provider한테 회원 프로필 받음
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // OAuth 서비스 이름(ex. kakao, naver, google)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // OAuth2 로그인 시 키(PK)가 되는 값
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        // 소셜 로그인에서 API가 제공하는 userInfo의 Json 값(유저 정보들)
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuthAttributes extractAttributes = OAuthAttributes.of(registrationId, userNameAttributeName, attributes);

        saveOrUpdateMember(extractAttributes);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(Role.ROLE_USER.getRole())),
                extractAttributes.oauth2UserInfo().getAttributes(),
                extractAttributes.nameAttributeKey()
        );
    }

    private void saveOrUpdateMember(OAuthAttributes extractAttributes) {
        Member member = memberRepository.findByEmail(extractAttributes.oauth2UserInfo().getEmail());

        // 같은 이메일로 일반 회원가입을 한 사용자의 경우 provider 업데이트 처리
        if (member != null && (member.getProvider() == null || member.getProviderId() == null)) {
            member.updateProvider(
                    extractAttributes.oauth2UserInfo().getProvider(),
                    extractAttributes.oauth2UserInfo().getProviderId()
            );
        } else {
            String password = UUID.randomUUID().toString().substring(8);

            member = Member.builder()
                    .email(extractAttributes.oauth2UserInfo().getEmail())
                    .password(passwordEncoder.encode(password))
                    .nickname(extractAttributes.oauth2UserInfo().getNickname())
                    .role(Role.ROLE_USER)
                    .provider(extractAttributes.oauth2UserInfo().getProvider())
                    .providerId(extractAttributes.oauth2UserInfo().getProviderId())
                    .build();
        }

        memberRepository.save(member);
    }
}
