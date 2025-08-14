package com.project.law.domain.user.dto;

import com.project.law.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final User user;

    public String getId() {
        return user.getId();
    }

    public String getRole(){
        return String.valueOf(user.getRole());
    }

    public String getEmail(){
        return user.getEmail();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
//                user.getPassword();
    }




    /**
     * 사용하지 않는 메소드
     * **/


    @Override
    public String getUsername(){
        return "";
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }
}
