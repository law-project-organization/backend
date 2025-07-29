package com.project.law.domain.user.service;

import com.project.law.domain.user.dto.response.UserInfoResponseDto;
import com.project.law.domain.user.entity.User;
import com.project.law.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * 유저 정보 조회
     * **/
    public ResponseEntity<?> getUserInfo(Authentication authentication) {
        // 유저 아이디 획득
        String userId = authentication.getPrincipal().toString();
        // 유저 조회
        User user = userRepository.findByIdAndDeleteYn(userId, false).orElseThrow(()-> new RuntimeException("No User Found, userId : " + userId)); // 성능상 Throw는 return에 비해 10배 이상 느릴 수 있으나 편의상 throw 사용
        // dto 빌드 후 응답
        return ResponseEntity.ok(UserInfoResponseDto.toDto(user));
    }


}
