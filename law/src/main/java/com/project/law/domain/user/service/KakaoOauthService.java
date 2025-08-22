package com.project.law.domain.user.service;


import com.project.law.common.util.CookieUtil;
import com.project.law.common.util.JwtUtil;
import com.project.law.domain.user.dto.response.KakaoTokenResponse;
import com.project.law.domain.user.dto.response.KakaoUserResponse;
import com.project.law.domain.user.entity.KakaoOauth;
import com.project.law.domain.user.entity.User;
import com.project.law.domain.user.repository.KakaoOauthRepository;
import com.project.law.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
//@RequiredArgsConstructor
@Slf4j
public class KakaoOauthService {

    @Value("${kakao.client.id}")
    private String clientId ;
    @Value("${kakao.redirect.uri}")
    private String redirectUri;
    @Value("${kakao.client.secret}")
    String clientSecret;
    private final WebClient kakaoOauthWebClient;
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final KakaoOauthRepository kakaoOauthRepository;
    private final UserRepository userRepository;

    public KakaoOauthService(@Qualifier("kakaoOauthWebClient") WebClient kakaoOauthWebClient,
                             JwtUtil jwtUtil, CookieUtil cookieUtil,
                             KakaoOauthRepository kakaoOauthRepository,
                             UserRepository userRepository){
        this.kakaoOauthWebClient = kakaoOauthWebClient;
        this.jwtUtil = jwtUtil;
        this.cookieUtil = cookieUtil;
        this.kakaoOauthRepository = kakaoOauthRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getKakaoLoginUrl() {
        log.info("login-url-responded");
        return ResponseEntity.ok("https://kauth.kakao.com/oauth/authorize?"
                + "client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&response_type=code");

//        @GetMapping("/oauth2/authorize/kakao")
//        public void redirectToKakao(HttpServletResponse res) throws IOException {
//            String url = UriComponentsBuilder
//                    .fromUriString("https://kauth.kakao.com/oauth/authorize")
//                    .queryParam("client_id", clientId)
//                    .queryParam("redirect_uri", redirectUri)
//                    .queryParam("response_type", "code")
//                    .toUriString();
//            res.sendRedirect(url);
//        }
    }

    /**
     * 카카오 Oauth Token으로 회원가입
     *
     * 추후 자체 로그인 구현 시
     * 신규 카카오 회원 가입 시 자체 회원가입 여부 확인 필요
     * **/
    @Transactional
    public CompletableFuture<Disposable> JoinWithKakaoOauthToken(String code, HttpServletResponse httpServletResponse) {
        log.info("kakaoAuthToken : {}", code);

        // 카카오 토큰 조회
        // 토큰에서 유저 정보 조회
        return CompletableFuture.completedFuture(fetchToken(code).subscribe(kakaoTokenResponse -> fetchUserInfo(kakaoTokenResponse.getAccessToken()).subscribe(
            kakaoUserResponse ->{
                    // 유저 정보 추출
                    Long socialOauthId = kakaoUserResponse.getSocialOauthId();
        log.info("socialOauthId : {}", socialOauthId);

        // 카카오 회원가입 유무에 따른 처리 (socialOauthId, email)
        Optional<KakaoOauth> optionalKakaoOauth = kakaoOauthRepository.findBySocialOauthId(socialOauthId);
        log.info("optionalKakaoOauth selected");
        // 기 가입된 유저 토큰 반환 처리
        if(optionalKakaoOauth.isPresent()){
            log.info("Already Joined Kakao Oauth User");

            // 토큰 발급을 위한 userId, userRole 획득
            KakaoOauth kakaoOauth = optionalKakaoOauth.get();
            String userId = kakaoOauth.getUser().getId();
            String userRole = kakaoOauth.getUser().getRole().toString();
            log.info("userId : {}, userRole : {}", userId, userRole);

            // jwt 토큰 발급 및 쿠키 저장
            generateJwtTokenAndPutInCookie(httpServletResponse, userId, userRole);

        }else {
            // Email 기반 로컬 회원가입 여부 조회
            // 로컬 회원이 있다면 KakaoOauth에 매핑
            // 추후 로직 추가

            // 로컬 회원가입 미가입 신규 회원가입 처리
            User newUser = User.builder().build();
            log.info("newUser is built");

            // 신규 유저 저장
            User user = userRepository.save(newUser);
            log.info("user saved");

            // KakaoOauth 빌드
            KakaoOauth kakaoOauth = KakaoOauth.builder()
                    .user(user)
                    .socialOauthId(socialOauthId)
                    .build();
            log.info("kakaoOauth is built");

            // kakaoOauth 저장
            kakaoOauthRepository.save(kakaoOauth);
            log.info("kakaoOauth is saved");

            // jwt token 발급
            String userId = user.getId();
            String userRole = user.getRole().name();
            log.info("userId : {}. userRole : {}", userId, userRole);

            // jwt 토큰 발급 및 쿠키 저장
            generateJwtTokenAndPutInCookie(httpServletResponse, userId, userRole);

        }
       } )));
    }

    /**
     * 1) 인가 코드를 받아 액세스 토큰으로 교환
     */
    public Mono<KakaoTokenResponse> fetchToken(String code) {
//        String url = "https://kauth.kakao.com/oauth/token";
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("grant_type", "authorization_code");
//        params.add("client_id", "카카오_앱키");
//        params.add("redirect_uri", "리다이렉트_주소");
//        params.add("code", code);
//
//        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
//
//        return restTemplate.postForObject(url, request, KakaoTokenResponse.class);
        return kakaoOauthWebClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/oauth/token")
                        .build()
                )
                .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                        .with("client_id", clientId)
                        .with("client_secret", clientSecret)
                        .with("redirect_uri", redirectUri)
                        .with("code", code))
                .retrieve()
                .bodyToMono(KakaoTokenResponse.class);
    }


    /**
     * 2) 받은 액세스 토큰으로 사용자 정보 조회
     */
    public Mono<KakaoUserResponse> fetchUserInfo(String accessToken) { // Mono<KakaoUserResponse>

//        String url = "https://kapi.kakao.com/v2/user/me";
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        headers.setBearerAuth(accessToken);
//
//        HttpEntity<HttpHeaders> request = new HttpEntity<>(headers);
//
//        return restTemplate.postForObject(url, request, KakaoUserResponse.class);

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



















    /** 응답 데이터 체크 **/
    public Mono<String> checkResponseData(String code) {
        log.info("kakaoAuthToken : {}", code);

//         return fetchToken(code).map(res -> fetchUserInfo(res.getAccessToken()));
        return null;
    }

}
