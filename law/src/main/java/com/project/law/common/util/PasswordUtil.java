package com.project.law.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PasswordUtil {

    public String bCryptPasswordEncoder(String password){
        String encodedPassword = new BCryptPasswordEncoder().encode(password);
        log.info("비밀번호 암호화 완료. 비밀번호 원본 : {}, 암호화된 비밀번호 : {}", password, encodedPassword);
        return encodedPassword;
    }
    
    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
