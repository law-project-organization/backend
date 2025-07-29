package com.project.law.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


// 유저 정보 응답
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoUserResponse {
    @JsonProperty(value = "id")
    private Long socialOauthId;
//    @JsonProperty(value = "kakao_account")
//    private KakaoAccount kakaoAccount;

}
