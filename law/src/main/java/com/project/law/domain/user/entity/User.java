package com.project.law.domain.user.entity;

import com.project.law.domain.user.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_user")
public class User {

    @PrePersist
    public void createId(){
        this.id = String.valueOf(UUID.randomUUID());
    }

    @Id @Column(name = "user_id")
    private String id;

    @Id @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role;

    @Builder.Default
    @Column(name = "delete_yn")
    private boolean deleteYn = false; // 기본형 이지만 명시적으로 false 할당
}
