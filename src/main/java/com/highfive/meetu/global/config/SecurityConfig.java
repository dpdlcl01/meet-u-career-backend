package com.highfive.meetu.global.config;

import com.highfive.meetu.global.security.CustomAccessDeniedHandler;
import com.highfive.meetu.infra.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 설정
 * - 권한 관리, 인증 처리 설정
 * - JWT 인증 필터 + ADMIN 보호 경로 적용
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // @PreAuthorize 등 사용 가능하게 설정
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF 설정 비활성화
            .csrf(AbstractHttpConfigurer::disable)

            // H2 콘솔, iframe 허용
            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))

            // 세션 미사용 (JWT 기반)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 인가 설정
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                    "/**",                                               // 임시 - 전체 인가 허용
                    "/api/admin/auth/login",                            // 관리자 로그인
                    "/api/business/auth/login",                         // 기업회원 로그인
                    "/api/personal/auth/login",                         // 개인회원 로그인
                    "/api/main/**",                                     // 메인
                    "/api/personal/job/**",                             // 채용 정보
                    "/api/community/popular/posts",                     // 커뮤니티 인기 게시글
                    "/api/personal/community/posts/all-posts",          // 커뮤니티 글 목록
                    "/api/personal/interview-reviews",                  // 면접 후기
                    "/ws/**",
                    "/ws-stomp/**",       // ✅ WebSocket STOMP endpoint
                    "/topic/**",          // ✅ 구독 채널
                    "/app/**",            // ✅ 발행 채널
                    "/api/chat/**",       // ✅ 채팅 REST API
                    "/swagger-ui/**",// WebSocket
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/error"
                ).permitAll()  // 인증 예외 경로
                .anyRequest().authenticated()  // 나머지 경로는 인증 필요
            )

            // 예외 처리 핸들러 등록
            .exceptionHandling(exceptionHandling ->
                exceptionHandling.accessDeniedHandler(customAccessDeniedHandler)
            )

            // JWT 필터 등록
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
