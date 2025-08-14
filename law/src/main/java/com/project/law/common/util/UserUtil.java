package com.project.law.common.util;

import com.project.law.domain.user.entity.User;
import com.project.law.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserUtil {

    private final UserRepository userRepository;
    
    /**
     * 이메일 기반 유저 존재 여부 확인
     * **/
    public Boolean existsUserByEmail(String email){
        return userRepository.existsByEmail(email);
    }

    /**
     * ID 기반 유저 조회
     * **/
    public User findUserById(String userId){
        User user = userRepository.findById(userId).orElseThrow( () -> new IllegalArgumentException("유저 조회 실패. 유저 ID : " + userId));
        log.info("유저 조회 완료. 유저 ID : {}", userId);
        return user;
    }

    /**
     * 이메일 기반 유저 조회
     * **/
    public User findUserByEmail(String email){
        User user = userRepository.findByEmail(email).orElseThrow( () -> new IllegalArgumentException("User Not Found : " + email));
        log.info("User Found Successfully : {}", email);
        return user;
    }
    
    /***
     * 유저 식별자 추출 메소드
     * **/
    public String extractUserIdFromAuthentication(Authentication authentication){
        String userId = String.valueOf(authentication.getPrincipal());
        log.info("userId Extracted Successfully : {}", userId);
        return userId;
    }
}
