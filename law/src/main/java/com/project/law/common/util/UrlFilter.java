package com.project.law.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UrlFilter {

    private static final String[] JWT_FILTER_PASS_PATH = {
            "/api/v1/auth",
            "/swagger",
            "/v3"
    };

    private static final String[] SECURITY_FILTER_PASS_PATH = {
            "/api/v1/auth/**",
            "/v3/api-docs/**",
            "/v3/swagger/**",
            "/swagger",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/api-docs/**"
    };

    private static final String LOG_OUT_PATH = "/api/v1/auth/logout";

    public static String[] getSecurityFilterPassPath(){
        return SECURITY_FILTER_PASS_PATH;
    }

    public static boolean checkIfPublicPath(String requestUri){
        log.info("requestUri : {}", requestUri);
        for(String path : JWT_FILTER_PASS_PATH){
            if(requestUri.startsWith(path)){
                return true;
            }
        }
        return false;
    }

    public static boolean checkIfLogoutPath(String requestUri){
        return LOG_OUT_PATH.equals(requestUri);
    }
}
