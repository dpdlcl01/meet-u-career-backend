package com.highfive.meetu.infra.jwt;

import com.highfive.meetu.domain.auth.personal.type.Role;
import com.highfive.meetu.domain.user.common.entity.Account;
import com.highfive.meetu.domain.user.common.entity.Admin;
import com.highfive.meetu.domain.user.common.entity.Profile;
import com.highfive.meetu.domain.user.common.repository.AccountRepository;
import com.highfive.meetu.domain.user.common.repository.AdminRepository;
import com.highfive.meetu.domain.user.common.repository.ProfileRepository;
import com.highfive.meetu.global.common.exception.NotFoundException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * JWT 생성 및 파싱 관리
 */
@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final ProfileRepository profileRepository;
    private final AccountRepository accountRepository;
    private final AdminRepository adminRepository;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    // JWT 서명용 Key 생성
    private Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Access Token 발급
     */
    public String generateAccessToken(Long accountId) {
        return generateToken(accountId, accessTokenExpiration);
    }

    /**
     * Refresh Token 발급
     */
    public String generateRefreshToken(Long accountId) {
        return generateToken(accountId, refreshTokenExpiration);
    }

    /**
     * 공통 토큰 생성 로직
     */
    private String generateToken(Long accountId, long expiration) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new NotFoundException("계정을 찾을 수 없습니다."));

        String role = getRoleForAccount(account);
        Long profileId = getProfileIdForAccount(account);

        return Jwts.builder()
            .setSubject(accountId.toString())
            .claim("accountId", accountId)
            .claim("profileId", profileId)
            .claim("role", role)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    // 회원의 Role 확인
    private String getRoleForAccount(Account account) {
        if (account.getAccountType() == Account.AccountType.PERSONAL) {
            return Role.PERSONAL.name();
        } else if (account.getAccountType() == Account.AccountType.BUSINESS) {
            return Role.BUSINESS.name();
        } else {
            // 관리자(Admin 테이블) 조회
            Admin admin = adminRepository.findByEmail(account.getEmail())
                .orElseThrow(() -> new NotFoundException("관리자 계정을 찾을 수 없습니다."));

            if (admin.getRole() == Admin.Role.SUPER) {
                return Role.SUPER.name();
            } else if (admin.getRole() == Admin.Role.ADMIN) {
                return Role.ADMIN.name();
            } else {
                throw new IllegalStateException("알 수 없는 관리자 권한입니다.");
            }
        }
    }

    // 개인회원만 profileId 반환
    private Long getProfileIdForAccount(Account account) {
        if (account.getAccountType() != Account.AccountType.PERSONAL) return null;

        Profile profile = profileRepository.findByAccountId(account.getId())
            .orElseThrow(() -> new NotFoundException("프로필을 찾을 수 없습니다."));
        return profile.getId();
    }

    /**
     * 토큰에서 단순 accountId 추출
     */
    public Long parseToken(String token) {
        return Long.valueOf(Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject());
    }

    /**
     * 토큰에서 Claims 전체 추출
     */
    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    /**
     * AccessToken 유효기간 조회
     */
    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    /**
     * RefreshToken 유효기간 조회
     */
    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }
}