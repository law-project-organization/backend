package com.project.law.domain.user.controller;

import com.project.law.domain.user.service.KakaoAuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth/kakao")
public class KakaoOauthController {

    private final KakaoAuthService kakaoAuthService;

    // XHR 요청 시 직접 redirect 가능
    @GetMapping("/login-url")
    public ResponseEntity<?> getKakaoLoginUrl(){
        return kakaoAuthService.getKakaoLoginUrl();
    }

    @GetMapping("/join")
    public ResponseEntity<?> JoinWithKakaoOauthToken(@RequestParam("code") String kakaoAuthToken,
                                                     HttpServletResponse httpServletResponse){
        return kakaoAuthService.JoinWithKakaoOauthToken(kakaoAuthToken, httpServletResponse);
    }

    @GetMapping("/check-response")
    public Mono<String> checkResponse(@RequestParam("code") String kakaoAuthToken){ // ResponseEntity<?>
        return kakaoAuthService.checkResponseData(kakaoAuthToken);
    }

}