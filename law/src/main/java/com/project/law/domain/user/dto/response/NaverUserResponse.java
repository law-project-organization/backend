package com.project.law.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


// 유저 정보 응답
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NaverUserResponse {
    @JsonProperty(value = "id")
    private Long socialOauthId;
//    @JsonProperty(value = "kakao_account")
//    private KakaoAccount kakaoAccount;

}
