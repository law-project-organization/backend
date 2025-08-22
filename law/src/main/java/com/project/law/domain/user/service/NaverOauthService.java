package com.project.law.domain.user.service;

import com.project.law.common.util.CookieUtil;
import com.project.law.common.util.JwtUtil;
import com.project.law.domain.user.dto.response.NaverTokenResponse;
import com.project.law.domain.user.dto.response.NaverUserResponse;
import com.project.law.domain.user.util.NaverWebClient;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class NaverOauthService {

    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;

    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;

    private final NaverWebClient naverWebClient;

    public NaverOauthService(@Qualifier("${naver.oauth.client.id}") String clientId,
                             @Qualifier("${naver.oauth.client.secret}") String clientSecret,
                             @Qualifier("${naver.redirect.uri}") String redirectUri,
                             JwtUtil jwtUtil,
                             CookieUtil cookieUtil,
                             @Qualifier("naverOauthWebClient") NaverWebClient naverWebClient) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
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
        return "https://nid.naver.com/oauth2.0/authorize?" +
                "response_type=" + "code" +
                "&client_id" + clientId +
                "&redirect_uri=" + URLEncoder.encode(redirectUri, Charset.defaultCharset()) +
                "&state=" + URLEncoder.encode(jwtUtil.generateAccessToken("userId","userRole"), Charset.defaultCharset())
                ;
    }

    /**
     *
     * **/
    public CompletableFuture<Disposable> oauthNaverLogin(String naverAuthToken, String state, HttpServletResponse httpServletResponse) {

        return CompletableFuture.completedFuture(fetchToken(naverAuthToken, state).subscribe( naverTokenResponse -> fetchUserInfo(naverTokenResponse.getAccessToken()).subscribe(
                naverUserResponse -> {
                    Long socialOauthId = naverUserResponse.getSocialOauthId();
                    log.info("socialOauthId : {}", socialOauthId);
                }
        )));
    }


        /**
         * 1) 인가 코드를 받아 액세스 토큰으로 교환
         */
        public Mono<NaverTokenResponse> fetchToken(String naverAuthToken, String state) {
            return WebClient.create("https://nid.naver.com/oauth2.0")
                    .post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/token")
                            .build()
                    )
                    .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                            .with("client_id", clientId)
                            .with("client_secret", clientSecret)
                            .with("code", naverAuthToken)
                            .with("state", state ) // URLEncoder.encode(jwtUtil.generateAccessToken("userId", "userRole")
                            )

                    .retrieve()
                    .bodyToMono(NaverTokenResponse.class);
        }



    /**
     *
     **/

    /**
     * 2) 받은 액세스 토큰으로 사용자 정보 조회
     */
    public Mono<NaverUserResponse> fetchUserInfo(String accessToken) { // Mono<KakaoUserResponse>
        return WebClient.create("https://openapi.naver.com")
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/nid/me")
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(NaverUserResponse.class);
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
