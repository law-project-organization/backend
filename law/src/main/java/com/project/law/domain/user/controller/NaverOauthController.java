package com.project.law.domain.user.controller;

import com.project.law.domain.user.service.NaverOauthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/oauth/naver")
public class NaverOauthController {

    private final NaverOauthService naverOauthService;

    @GetMapping("/login-url")
    public ResponseEntity<?> getOauthNaverLoginUrl(){
        return ResponseEntity.ok(naverOauthService.getOauthNaverLoginUrl());
    }

    @PostMapping("login")
    public ResponseEntity<Mono<Object>> oauthNaverLogin(String code){
        return ResponseEntity.ok(naverOauthService.oauthNaverLogin());

    }
}
