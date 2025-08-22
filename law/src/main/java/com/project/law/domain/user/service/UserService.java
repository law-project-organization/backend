package com.project.law.domain.user.service;

import com.project.law.common.util.PasswordUtil;
import com.project.law.common.util.UserUtil;
import com.project.law.domain.user.dto.response.DecreaseUserFreeTrierCountResDto;
import com.project.law.domain.user.dto.response.GetUserFreeTrierCountResDto;
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
    private final UserUtil userUtil;
    private final PasswordUtil passwordUtil;

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

    /**
     * 소장 일일 이용 횟수 조회
     * **/
    public GetUserFreeTrierCountResDto getUserFreeTrierCount(Authentication authentication) {
        String userId = userUtil.extractUserIdFromAuthentication(authentication);
        int freeTrierCount = userUtil.findUserById(userId).getFreeTrierCount();
        return new GetUserFreeTrierCountResDto(freeTrierCount);
    }
    
    /**
     * 일일 소장 작성 이용 횟수 차감
     * **/
    public DecreaseUserFreeTrierCountResDto decreaseUserFreeTrierCount(Authentication authentication) {
        User user = userUtil.findUserById(userUtil.extractUserIdFromAuthentication(authentication));
        user.setFreeTrierCount(user.getFreeTrierCount() - 1);
        User updatedUser = userRepository.save(user);
        log.info("User Free Trier Count 차감 완료");
        return new DecreaseUserFreeTrierCountResDto(updatedUser.getFreeTrierCount());
    }
}
