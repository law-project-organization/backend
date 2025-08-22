package com.project.law.domain.user.dto.request;

import com.project.law.domain.user.entity.User;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Valid
public class LocalJoinRequestDto {

    @Length(min = 4, max = 50)
    private String email;

    @Length(min = 4, max = 8)
    private String password;

    @Length(min = 4, max = 8)
    private String passwordCheck;

    public User toUser(String email, String password){
        return User.builder()
                .email(email)
                .password(password)
                .build();
    }
}
