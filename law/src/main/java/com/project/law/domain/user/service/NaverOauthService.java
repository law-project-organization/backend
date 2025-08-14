package com.project.law.domain.user.service;

import com.project.law.common.util.CookieUtil;
import com.project.law.common.util.JwtUtil;
import com.project.law.domain.user.dto.response.KakaoUserResponse;
import com.project.law.domain.user.dto.response.NaverTokenResponse;
import com.project.law.domain.user.util.NaverWebClient;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class NaverOauthService {

    private final String code;
    private final String clientId;
    private final String state;
    private final String redirectUri;

    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;

    private final NaverWebClient naverWebClient;

    public NaverOauthService(@Qualifier("${naver.oauth.code}") String code,
                             @Qualifier("${naver.oauth.client.id}") String clientId,
                             @Qualifier("${naver.oauth.state}") String state,
                             @Qualifier("${naver.oauth.redirect.uri}") String redirectUri, JwtUtil jwtUtil, CookieUtil cookieUtil,
                             @Qualifier("naverOauthWebClient") NaverWebClient naverWebClient) {
        this.code = code;
        this.clientId = clientId;
        this.state = state;
        this.redirectUri = redirectUri;
        this.jwtUtil = jwtUtil;
        this.cookieUtil = cookieUtil;
        this.naverWebClient = naverWebClient;
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


        /**
         * 1) 인가 코드를 받아 액세스 토큰으로 교환
         */
        public Mono<NaverTokenResponse> fetchToken(String code) {
            return WebClient.create("https://openapi.naver.com/v1/nid/me")
                    .post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/oauth/token")
                            .build()
                    )
                    .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                            .with("client_id", clientId)
//                            .with("client_secret", clientSecret)
                            .with("redirect_uri", redirectUri)
                            .with("code", code))
                    .retrieve()
                    .bodyToMono(NaverTokenResponse.class);
        }



    /**
     *
     **/

    /**
     * 2) 받은 액세스 토큰으로 사용자 정보 조회
     */
    public Mono<KakaoUserResponse> fetchUserInfo(String accessToken) { // Mono<KakaoUserResponse>
        return WebClient.create("https://kapi.kakao.com")
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/user/me")
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(KakaoUserResponse.class);
    }

    /**
     * jwt token 발급 후 쿠키에 저장하는 메소드
     * **/
    public void generateJwtTokenAndPutInCookie(HttpServletResponse httpServletResponse, String userId, String userRole){
        // jwt 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(userId, userRole);
        String refreshToken = jwtUtil.generateRefreshToken(userId, userRole);
        log.info("accessToken : {}, refreshToken : {}", accessToken, refreshToken);


        // 쿠키에 토큰 추가
        httpServletResponse.addCookie(cookieUtil.generateNewAccessTokenCookie(accessToken));
        httpServletResponse.addCookie(cookieUtil.generateNewRefreshTokenCookie(refreshToken));
        log.info("jwt token is put in cookie");
    }
}
