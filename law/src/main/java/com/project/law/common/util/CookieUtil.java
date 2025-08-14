package com.project.law.common.util;

import com.project.law.common.enums.CookieName;
import com.project.law.common.enums.JwtTokenType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    public Cookie generateNewIsLoggedInCookie(){
        Cookie cookie = new Cookie(CookieName.IS_LOGGED_IN.getCookieName(), "true");
        cookie.setHttpOnly(false);
        cookie.setSecure(false);
        cookie.setMaxAge(86400);
        cookie.setPath("/");
        return cookie;
    }

    public Cookie generateNewAccessTokenCookie(String accessToken){
        Cookie cookie = new Cookie(JwtTokenType.ACCESS_TOKEN_NAME.getTokenName(), accessToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(3600);
        cookie.setPath("/");
        return cookie;
    }

    public Cookie generateNewRefreshTokenCookie(String refreshToken){
        Cookie cookie = new Cookie(JwtTokenType.REFRESH_TOKEN_NAME.getTokenName(), refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(86400);
        cookie.setPath("/");
        return cookie;
    }

    public Cookie generateLogoutIsLoggedInCookie(){
        Cookie cookie = new Cookie(CookieName.IS_LOGGED_IN.getCookieName(), "false");
        cookie.setHttpOnly(false);
        cookie.setSecure(false);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        return cookie;
    }

    public Cookie generateLogoutAccessTokenCookie(){
        Cookie cookie = new Cookie(JwtTokenType.ACCESS_TOKEN_NAME.getTokenName(), null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        return cookie;
    }

    public Cookie generateLogoutRefreshTokenCookie(){
        Cookie cookie = new Cookie(JwtTokenType.REFRESH_TOKEN_NAME.getTokenName(), null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        return cookie;
    }

    public String getAuthToken(HttpServletRequest httpServletRequest){
        Cookie[] cookies = httpServletRequest.getCookies();
        if(cookies != null){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals(HttpHeaders.AUTHORIZATION)){
                    return cookie.getName();
                }
            }
        }
        return null;
    }


}
