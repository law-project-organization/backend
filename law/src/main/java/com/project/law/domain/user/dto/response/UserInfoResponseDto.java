package com.project.law.domain.user.dto.response;

import com.project.law.domain.user.entity.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoResponseDto {

    private String email;

    private String role;

    public static UserInfoResponseDto toDto(User user){
        return UserInfoResponseDto.builder()
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}


