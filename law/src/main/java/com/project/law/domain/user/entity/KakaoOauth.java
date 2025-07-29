package com.project.law.domain.user.entity;

import com.project.law.domain.base.entity.BaseTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "tb_kakao_oauth")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class KakaoOauth extends BaseTime {

    @PrePersist
    private void perPersistGenerateId(){
        this.id = String.valueOf(UUID.randomUUID());
    }

    @Id
    @Column(name = "kakao_oauth_id")
    private String id;

    @OneToOne(optional = false, cascade = {})
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "social_oauth_id")
    private Long socialOauthId;

}
