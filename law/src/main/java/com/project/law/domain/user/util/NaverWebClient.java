package com.project.law.domain.user.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class NaverWebClient {

    @Bean(name = "naverOauthWebClient")
    public WebClient naverOauthWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("https://openapi.naver.com/v1/nid/me")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
