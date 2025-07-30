package com.project.law.common.filter;

import com.project.law.common.util.JwtUtil;
import com.project.law.common.util.UrlFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.io.PrintWriter;

@Component
@Slf4j
public class CustomLogoutFilter extends GenericFilterBean {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtUtil jwtUtil;

    public CustomLogoutFilter(@Qualifier("CustomStringRedisTemplate") RedisTemplate<String, String> redisTemplate, JwtUtil jwtUtil) {
        this.redisTemplate = redisTemplate;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        doFilter((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, filterChain);
    }

    public void doFilter(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        
        // 로그아웃 경로와 일치하지 않으면 필터 패스
        if(!UrlFilter.checkIfLogoutPath(httpServletRequest.getRequestURI())){ filterChain.doFilter(httpServletRequest, httpServletResponse); }

        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        log.info("logout userId : {}", userId);

        // 만약 로그인된 유저가 아니면 예외 처리 / 로그인 된 유저는 해당 없음
        if(redisTemplate.delete(jwtUtil.toRedisRefreshTokenKey(userId))){
            httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
            
            httpServletResponse.setStatus(401);
            PrintWriter printWriter = httpServletResponse.getWriter();
            printWriter.print("Not Logged In User");
            printWriter.close();
        }

        httpServletResponse.setStatus(204);
    }
}
