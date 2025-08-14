package com.project.law.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.law.common.util.CookieUtil;
import com.project.law.common.util.JwtUtil;
import com.project.law.domain.user.dto.CustomUserDetails;
import com.project.law.domain.user.dto.request.LocalLoginRequestDto;
import com.project.law.domain.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
public class CustomLoginFilter extends AbstractAuthenticationProcessingFilter { // LoginFilter의 SuperClass

        /** 필드 **/
//        private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/api/v1/admins/login", "POST");
        private RequestMatcher requiresAuthenticationRequestMatcher;
        private boolean postOnly = true;
        private final AuthenticationManager authenticationManager;
        private final JwtUtil jwtUtil;
        private final CookieUtil cookieUtil;
        private final BCryptPasswordEncoder encoder;
        private final RedisTemplate<String, String> redisTemplate;
        private final UserRepository userRepository;
        private final ObjectMapper objectMapper;
        /** 생성자 */
        public CustomLoginFilter(AuthenticationManager authenticationManager,
                                 JwtUtil jwtUtil,
                                 CookieUtil cookieUtil,
                                 BCryptPasswordEncoder encoder,
                                 @Qualifier(value = "CustomStringRedisTemplate") StringRedisTemplate redisTemplate, UserRepository userRepository, ObjectMapper objectMapper) {
            super("/api/v1/auth/login");
            setAuthenticationManager(authenticationManager);
//            super(DEFAULT_ANT_PATH_REQUEST_MATCHER.getPattern());
            this.jwtUtil = jwtUtil;
            this.cookieUtil = cookieUtil;
            this.authenticationManager = authenticationManager;
            this.encoder = encoder;
            this.redisTemplate = redisTemplate;
            this.userRepository = userRepository;
            this.objectMapper = objectMapper;
        }

        /** 인증 시도 **/
        @Override
        public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {
            /** 로그인 요청 경로 검증 **/

            if (this.postOnly && !request.getMethod().equalsIgnoreCase(HttpMethod.POST.name())) {
                throw new AuthenticationServiceException("Unsupported Method : " + request.getMethod());
            } else {
                LocalLoginRequestDto loginRequestDTO;
                try {
                    /** JSON 파싱 **/
                    loginRequestDTO = objectMapper.readValue(request.getInputStream(), LocalLoginRequestDto.class);
                    log.info("loginRequestDTO successfully parsed");
                } catch (IOException e) {
                    throw new RuntimeException("[Login Filter] Parsing Failed : ", e);
                }

                /** 유저네임, 비밀번호 획득 **/
                String email = loginRequestDTO.getEmail();
                String password = loginRequestDTO.getPassword();
                log.info("email : {}, password : {}", email, password);
                /** 전처리 **/
                /** 토큰화 **/
                UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken.unauthenticated(email, password);
                log.info("Login Requested email : {}", email);

                /** (필요 시)다중 로그인 방지 로직 추가 **/
                // redis refreshtoken 존재 여부 확인
                // 있으면 빠꾸

                /** 인증 **/
                return this.authenticationManager.authenticate(token);
            }
        }

        /** 로그인 성공 시 **/
        protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
            log.info("Login Success Logic Start");
    
            // 유저 정보 획득 (UsernamePasswordAuthenticationToken이 authResult에 담김)
            CustomUserDetails customUserDetails = (CustomUserDetails) authResult.getPrincipal();
            String userId = customUserDetails.getId();
            String role = customUserDetails.getRole();
            log.info("userId : {}, role : {}", userId, role );

            // jwt token 발급
            String accessToken = jwtUtil.generateAccessToken(userId ,role);
            String refreshToken = jwtUtil.generateRefreshToken(userId, role);
            log.info("accessToken : {}, \n refreshToken : {}", accessToken, refreshToken);

            // refresh token redis 저장 (id_tokenName)
            redisTemplate.opsForValue().set(jwtUtil.toRedisRefreshTokenKey(userId), refreshToken);
            log.info("Redis Refresh Token Saved Successfully : userId : {}, refreshToken : {}",userId, refreshToken);

            // content-type (없어도 자동으로 직렬화 해주긴 합니다 / 명시적인 것을 선호하는 타입)
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            // jwt token은 cookie에 httponly, secure 적용을 기준으로 개발 했습니다.
            response.addCookie(cookieUtil.generateNewAccessTokenCookie(accessToken));
            response.addCookie(cookieUtil.generateNewRefreshTokenCookie(accessToken));
            response.addCookie(cookieUtil.generateNewIsLoggedInCookie());
            log.info("Cookie Set Successfully");

            response.setStatus(200);


            PrintWriter printWriter = response.getWriter();
            printWriter.print("{\"success\" : \"true\"}");
            printWriter.close();
        }

        /** 로그인 실패 시 **/
        protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
            log.info("Login Fail Logic Start");

            response.setStatus(401);

            PrintWriter printWriter = response.getWriter();
            printWriter.println("Login Failed, Failed Message For Development");
            printWriter.print("cause : " + failed.getMessage());
            printWriter.close();
        }
}
