package com.project.law.domain.user.repository;

import com.project.law.domain.user.entity.KakaoOauth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KakaoOauthRepository extends JpaRepository<KakaoOauth, String> {

    Optional<KakaoOauth> findBySocialOauthId(Long socialOauthId);
}
