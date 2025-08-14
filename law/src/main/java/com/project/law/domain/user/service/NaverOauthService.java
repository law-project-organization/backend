package com.project.law.domain.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class NaverOauthService {

    private final String code;
    private final String clientId;
    private final String state;
    private final String redirectUri;

    public NaverOauthService(@Qualifier("${naver.oauth.code}") String code,
                             @Qualifier("${naver.oauth.client.id}") String clientId,
                             @Qualifier("${naver.oauth.state}") String state,
                             @Qualifier("${naver.oauth.redirect.uri}") String redirectUri) {
        this.code = code;
        this.clientId = clientId;
        this.state = state;
        this.redirectUri = redirectUri;
    }


    /**
     * 네이버 로그인 URL 조회
     * **/
    public String getOauthNaverLoginUrl() {
        // const url = `https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=${client_id}&state=${state}&redirect_uri=${redirect_uri}`;
        return new StringBuilder().append("https://nid.naver.com/oauth2.0/authorize?")
                .append("response_type=").append("code")
                .append("&client_id").append("clientId")
                .append("&state=").append("state")
                .append("&redirect_uri=").append("redirect_uri")
                .toString();
    }

    /**
     *
     * **/
    public Mono<Object> oauthNaverLogin() {
                return null;
    }

    public Mono<NaverOauthResponse> fetchToken(String code){

//        https://openapi.naver.com/v1/nid/me

    }

    /**
     *
     **/
}
