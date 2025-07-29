package com.project.law.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

// 토큰 응답
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class KakaoTokenResponse {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("expires_in")
    private int expiresIn;
    // … 기타 필드
}
