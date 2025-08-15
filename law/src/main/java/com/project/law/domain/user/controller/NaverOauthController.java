package com.project.law.domain.user.controller;

import com.project.law.domain.user.service.NaverOauthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.Disposable;

import java.util.concurrent.CompletableFuture;

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
    public ResponseEntity<CompletableFuture<Disposable>> oauthNaverLogin(@RequestParam("code") String naverAuthToken,
                                                                         @RequestParam("state") String state,
                                                                         @RequestParam("error") String error,
                                                                         @RequestParam("error_description") String errorDescription,
                                                                         HttpServletResponse httpServletResponse){
        log.info("code : {}, state : {}, error : {}, error_description : {}",naverAuthToken, state, error, errorDescription  );
        return ResponseEntity.ok(naverOauthService.oauthNaverLogin(naverAuthToken, state, httpServletResponse));

    }
}
