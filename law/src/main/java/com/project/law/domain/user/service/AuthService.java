package com.project.law.domain.user.service;

import com.project.law.common.util.PasswordUtil;
import com.project.law.common.util.UserUtil;
import com.project.law.domain.user.dto.request.LocalJoinRequestDto;
import com.project.law.domain.user.entity.User;
import com.project.law.domain.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserUtil userUtil;
    private final PasswordUtil passwordUtil;

    /**
     * 로컬 회원가입
     * **/
    public void localJoin(@Valid LocalJoinRequestDto dto) {

        String email = dto.getEmail();

        if(userUtil.existsUserByEmail(email)){
            throw new IllegalArgumentException("Already Exist Email : " + email);
        }

        String password = dto.getPassword();
        String passwordCheck = dto.getPasswordCheck();

        if(!password.equals(passwordCheck)){
            throw new IllegalArgumentException("Password Not Match. password : " + password + "\n passwordCheck : " + passwordCheck);
        }

        String encodedPassword = passwordUtil.bCryptPasswordEncoder(password);

        User user = dto.toUser(email, encodedPassword);
        log.info("User Built Successfully");

        userRepository.save(user);
        log.info("Uer Saved Successfully");
    }
}
