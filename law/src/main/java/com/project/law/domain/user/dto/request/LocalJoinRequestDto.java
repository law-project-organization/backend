package com.project.law.domain.user.dto.request;

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

    @Length(min = 4, max = 10)
    private String username;

    @Length(min = 4, max = 8)
    private String password;

    @Length(min = 4, max = 8)
    private String passwordCheck;
}
