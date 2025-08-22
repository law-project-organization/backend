package com.project.law.common.util;

import com.project.law.common.enums.JwtTokenType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    public static Cookie generateAccessTokenCookie(String accessToken){
        Cookie cookie = new Cookie(JwtTokenType.ACCESS_TOKEN_NAME.getTokenName(), accessToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        return cookie;
    }

    public static Cookie generateRefreshTokenCookie(String refreshToken){
        Cookie cookie = new Cookie(JwtTokenType.REFRESH_TOKEN_NAME.getTokenName(), refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        return cookie;
    }

    public static String getAuthToken(HttpServletRequest httpServletRequest){
        Cookie[] cookies = httpServletRequest.getCookies();
        for(Cookie cookie : cookies){
            if(cookie.getName().equals(HttpHeaders.AUTHORIZATION)){
                return cookie.getName();
            }
        }
        return null;
    }


}
