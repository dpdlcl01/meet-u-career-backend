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
 * - 사용자/관리자별 AccessToken, RefreshToken 발급
 * - Claim: accountId/adminId, profileId(선택), role 포함
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

    /**
     * 서명 키 반환 (HMAC-SHA256용 Key)
     */
    private Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 개인/기업 회원용 AccessToken 발급
     */
    public String generateAccessToken(Long accountId) {
        return generateUserToken(accountId, accessTokenExpiration);
    }

    /**
     * 개인/기업 회원용 RefreshToken 발급
     */
    public String generateRefreshToken(Long accountId) {
        return generateUserToken(accountId, refreshTokenExpiration);
    }

    /**
     * 관리자용 AccessToken 발급
     */
    public String generateAdminAccessToken(Long adminId) {
        return generateAdminToken(adminId, accessTokenExpiration);
    }

    /**
     * 관리자용 RefreshToken 발급
     */
    public String generateAdminRefreshToken(Long adminId) {
        return generateAdminToken(adminId, refreshTokenExpiration);
    }

    /**
     * 개인/기업 회원용 JWT 생성
     * - accountId, role, profileId 포함
     */
    private String generateUserToken(Long accountId, long expiration) {
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

    /**
     * 관리자용 JWT 생성
     * - adminId, role 포함 (profileId 없음)
     */
    private String generateAdminToken(Long adminId, long expiration) {
        Admin admin = adminRepository.findById(adminId)
            .orElseThrow(() -> new NotFoundException("관리자 계정을 찾을 수 없습니다."));

        Role role = admin.getRole() == Admin.Role.SUPER ? Role.SUPER : Role.ADMIN;

        return Jwts.builder()
            .setSubject(adminId.toString())
            .claim("accountId", adminId)
            .claim("role", role.name())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    /**
     * 계정에서 role 추출
     */
    private String getRoleForAccount(Account account) {
        if (account.getAccountType() == Account.AccountType.PERSONAL) {
            return Role.PERSONAL.name();
        } else if (account.getAccountType() == Account.AccountType.BUSINESS) {
            return Role.BUSINESS.name();
        } else {
            // 관리자 처리
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

    /**
     * 개인회원인 경우 profileId 추출
     */
    private Long getProfileIdForAccount(Account account) {
        if (account.getAccountType() != Account.AccountType.PERSONAL) return null;

        Profile profile = profileRepository.findByAccountId(account.getId())
            .orElseThrow(() -> new NotFoundException("프로필을 찾을 수 없습니다."));
        return profile.getId();
    }

    /**
     * JWT에서 accountId/adminId 추출 (subject 기반)
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
     * JWT에서 Claim 전체 추출
     */
    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    /**
     * AccessToken 만료 시간 반환
     */
    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    /**
     * RefreshToken 만료 시간 반환
     */
    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }
}
