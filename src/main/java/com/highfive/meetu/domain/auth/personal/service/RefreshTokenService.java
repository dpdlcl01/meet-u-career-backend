package com.highfive.meetu.domain.auth.personal.service;

import com.highfive.meetu.infra.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Redis 기반 RefreshToken 관리 서비스
 * - 저장/조회/삭제/검증 서비스
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final JwtProvider jwtProvider;
    private final StringRedisTemplate redisTemplate;

    /**
     * RefreshToken 저장
     *
     * @param accountId 사용자 ID
     * @param refreshToken 저장할 RefreshToken
     * @param expirationMillis 만료 시간 (밀리초)
     */
    public void saveRefreshToken(Long accountId, String refreshToken, long expirationMillis) {
        redisTemplate.opsForValue().set(
            generateKey(accountId),
            refreshToken,
            expirationMillis,
            TimeUnit.MILLISECONDS
        );
    }

    /**
     * RefreshToken 조회
     *
     * @param accountId 사용자 ID
     * @return 저장된 RefreshToken (없으면 null)
     */
    public String getRefreshToken(Long accountId) {
        return redisTemplate.opsForValue().get(generateKey(accountId));
    }

    /**
     * RefreshToken 삭제 (로그아웃 시 사용)
     *
     * @param accountId 사용자 ID
     */
    public void deleteRefreshToken(Long accountId) {
        redisTemplate.delete(generateKey(accountId));
    }

    /**
     * Redis 저장 Key 생성
     *
     * @param accountId 사용자 ID
     * @return Redis Key 문자열
     */
    private String generateKey(Long accountId) {
        return "refreshToken:" + accountId;
    }

    /**
     * RefreshToken을 검증하고 새 AccessToken을 발급하는 메서드
     */
    public String reissueAccessToken(String refreshToken) {
        Long accountId = jwtProvider.parseToken(refreshToken);

        String redisRefreshToken = getRefreshToken(accountId);
        if (redisRefreshToken == null || !redisRefreshToken.equals(refreshToken)) {
            throw new RuntimeException("RefreshToken이 유효하지 않습니다. 다시 로그인 해주세요.");
        }

        return jwtProvider.generateAccessToken(accountId);
    }
}
