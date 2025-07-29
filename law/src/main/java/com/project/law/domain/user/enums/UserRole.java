package com.project.law.domain.user.enums;

public enum UserRole {
    USER("user"),
    ADMIN("admin");

    private final String korean;

    UserRole(String korean){
        this.korean = korean;
    }

    public UserRole findUserRoleWithKorean(String input){
        for(UserRole userRole : values()){
            if(userRole.korean.equalsIgnoreCase(input)){
                return userRole;
            }
        }
        throw new IllegalArgumentException("Not Proper UserRole");
    }
}
