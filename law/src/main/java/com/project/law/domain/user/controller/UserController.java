package com.project.law.domain.user.controller;

import com.project.law.domain.user.dto.response.DecreaseUserFreeTrierCountResDto;
import com.project.law.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/user-info")
    public ResponseEntity<?> getUserInfo(Authentication authentication){
        return userService.getUserInfo(authentication);
    }
        
    /**
     * 추후 캐싱기능 도입 가능
     * 아래 주석 해제 ( + 업데이트 )
     * **/
//    @Cacheable(key = "`freeTrierCount:` + #authentication.principal")
    @Tag(name = "유저")
    @Operation(summary = "유저 일일 무료 이용 횟수 조회")
    @GetMapping("/free-trier")
    public ResponseEntity<?> getUserFreeTrierCount(Authentication authentication){
        return ResponseEntity.ok(userService.getUserFreeTrierCount(authentication));
    }

    // swagger
    @Tag(name = "유저")
    @Operation(summary = "유저 일일 무료 이용 횟수 차감")
    @PatchMapping("/free-trier")
    public ResponseEntity<DecreaseUserFreeTrierCountResDto> decreaseUserFreeTrierCount(Authentication authentication){
        return ResponseEntity.ok(userService.decreaseUserFreeTrierCount(authentication));
    }

}
