package com.project.law.domain.user.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@Configuration
public class KakaoRestTemplate {

    @Bean(name = "customRestTemplate")
    @Primary
    RestTemplate customRestTemplate(){
        return new RestTemplate();
    }
}
