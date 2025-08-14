package com.project.law.domain.user.controller;

import com.project.law.domain.user.service.KakaoOauthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth/kakao")
public class KakaoOauthController {

    private final KakaoOauthService kakaoAuthService;
    private final KakaoOauthService kakaoOauthService;

    // XHR 요청 시 직접 redirect 가능
    @GetMapping("/login-url")
    public ResponseEntity<?> getKakaoLoginUrl(){
        return kakaoOauthService.getKakaoLoginUrl();
    }

    @GetMapping("/join")
    public ResponseEntity<CompletableFuture<Disposable>> JoinWithKakaoOauthToken(@RequestParam("code") String kakaoAuthToken,
                                                                                 HttpServletResponse httpServletResponse) {
        return new ResponseEntity<>(kakaoAuthService.JoinWithKakaoOauthToken(kakaoAuthToken, httpServletResponse), HttpStatus.CREATED);
    }

    @GetMapping("/check-response")
    public Mono<String> checkResponse(@RequestParam("code") String kakaoAuthToken){ // ResponseEntity<?>
        return kakaoOauthService.checkResponseData(kakaoAuthToken);
    }

}