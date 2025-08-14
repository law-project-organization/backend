package com.project.law.common.filter;

import com.project.law.common.util.CookieUtil;
import com.project.law.common.util.JwtUtil;
import com.project.law.common.util.UrlFilter;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;

@Slf4j
public class CustomJwtFilter extends OncePerRequestFilter { // OncePerRequestFilter를 쓰고 GenericFilterBean을 쓰는 이유 : Redirect 시에 중복 호출 방지

    /** 필드 **/
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;

    /** 생성자 **/
    public CustomJwtFilter(JwtUtil jwtUtil, CookieUtil cookieUtil) {
        this.jwtUtil = jwtUtil;
        this.cookieUtil = cookieUtil;
    }


    /** 필더 검증 **/
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 공개 경로 여부 확인
        if(UrlFilter.checkIfPublicPath(request.getRequestURI())){
            log.info("Is Public Endpoint");
            filterChain.doFilter(request, response);
            return ;
        }

        // accessToken 검증
        String bearerToken = cookieUtil.getAuthToken(request);
        if(bearerToken == null || !bearerToken.startsWith("Bearer ")){
            log.info("[JwtFilter] bearerToken is Null or bearerToken Not Start With Bearer");
            filterChain.doFilter(request, response);
            return;
        }

        // Bearer 접두어 분리
        String token = bearerToken.substring(7);

        // 유효성 검사
        if(jwtUtil.isExpired(token)){
            log.info("[JwtFilter] JwtToken Is Expired");

            response.setStatus(401);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            PrintWriter writer = response.getWriter();
            writer.print("Access Token Is Expired");
            return;
        }

        /** 인증 객체 저장 **/
        try{
            String userId = jwtUtil.getId(token);
            String userRole = jwtUtil.getRole(token);
            log.info("[JwtFilter] userId : {}", userId);
            // 추후 권한 추가
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userId, null, Collections.singletonList(new SimpleGrantedAuthority(userRole)));

            // IP 등 추가적인 세부 정보를 저장
//            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 컨텍스트에 인증정보 저장, 이후 필터들에서는 이미 인증된 객체로 인식
            SecurityContextHolder.getContext().setAuthentication(authToken);

        }catch (JwtException jwtException){
            log.info("JwtException Caused");
            throw jwtException;
        }

        /** 다음 필터 호출 **/
        log.info("[JwtFilter] Next Filter");
        filterChain.doFilter(request, response);
    }
}
