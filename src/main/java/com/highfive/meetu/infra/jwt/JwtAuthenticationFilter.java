
package com.highfive.meetu.infra.jwt;

import com.highfive.meetu.domain.auth.personal.type.Role;
import com.highfive.meetu.domain.user.common.repository.ProfileRepository;
import com.highfive.meetu.global.security.CustomUserPrincipal;
import com.highfive.meetu.global.util.JwtErrorResponseUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT 인증 필터
 * - Authorization 헤더로부터 토큰 추출
 * - 토큰 유효성 검사 후 Authentication 설정
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final ProfileRepository profileRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                Claims claims = jwtProvider.parseClaims(token);

                // 기본 accountId로 principal 생성
                Long accountId = Long.valueOf(claims.get("accountId").toString());

                // accountId로 profileId 조회 (개인회원만 존재)
                Long profileId = claims.get("profileId") != null ? Long.valueOf(claims.get("profileId").toString()) : null;
                Role role = Role.valueOf(claims.get("role").toString());

                CustomUserPrincipal userPrincipal = new CustomUserPrincipal(accountId, profileId, role);

                // Authentication 등록
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userPrincipal, null, List.of()
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                JwtErrorResponseUtil.sendUnauthorized(response, "유효하지 않거나 만료된 토큰입니다.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
