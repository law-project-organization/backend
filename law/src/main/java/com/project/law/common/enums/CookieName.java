package com.project.law.common.enums;

import lombok.Getter;

@Getter
public enum CookieName {

    ACCESS_TOKEN("accessToken"),
    REFRESH_TOKEN("refreshToken"),
    IS_LOGGED_IN("isLoggedIn");

    private final String cookieName;

    CookieName(String cookieName){
        this.cookieName = cookieName;
    }
}
