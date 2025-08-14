package com.project.law.common.util;

import com.project.law.common.enums.JwtTokenType;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    private final Long ACCESS_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60L; // 1시간 // 임시 1년
    private final Long REFRESH_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24L; // 1일
    private final String SECRET = "ThIsIsNoTsEcReTkEyThIsIsNoTsEcReTkEy"; // 시크릿 키
    private final SecretKey SECRET_KEY;

    /** 생성자 **/
    public JwtUtil(){
        this.SECRET_KEY = new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    /** 토큰 만료 여부 검증 **/
    public boolean isExpired(String token){
        return Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    /** 유저 식별자 획득 **/
    public String getId(String token){
        return Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token).getPayload().get("id", String.class);
    }

    /** 유저 네임 획득 **/
    public String getUsername(String token){
        return Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    /** 유저 권한 획득 **/
    public String getRole(String token){
        return Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    /** AccessToken 발급  **/
    public String generateAccessToken(String userId, String userRole){
        return Jwts.builder()
                .claim("id", userId)
                .claim("role", userRole)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    /** RefreshToken 발급  **/
    public String generateRefreshToken(String userId, String userRole){
        return Jwts.builder()
                .claim("id", userId)
                .claim("role", userRole)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }
    
    /** redis에 저장할 refresh token key 값 생성 **/
    public String toRedisRefreshTokenKey(String userId){
        return String.format("%s_%s", userId, JwtTokenType.REFRESH_TOKEN_NAME.getTokenName());
    }
}
