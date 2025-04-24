package com.highfive.meetu.domain.auth.personal.controller;

import com.highfive.meetu.domain.auth.personal.dto.LoginResponseDTO;
import com.highfive.meetu.domain.auth.personal.service.RefreshTokenService;
import com.highfive.meetu.global.common.response.ResultData;
import com.highfive.meetu.infra.jwt.JwtProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

/**
 * 인증(Authentication) 관련 API 컨트롤러
 * - AccessToken 재발급
 * - 로그아웃 처리 포함
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/personal/auth")
public class AuthController {

    private final RefreshTokenService refreshTokenService;
    private final JwtProvider jwtProvider;

    /**
     * AccessToken 재발급 API
     */
    @GetMapping("/refresh")
    public ResultData<LoginResponseDTO> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshToken(request);

        if (refreshToken == null) {
            return ResultData.fail("RefreshToken이 없습니다. 다시 로그인해주세요.");
        }

        // 서비스 호출: RefreshToken 검증하고 새 AccessToken 발급
        String newAccessToken = refreshTokenService.reissueAccessToken(refreshToken);

        // 새 AccessToken을 쿠키로 내려줌
        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", newAccessToken)
            .httpOnly(true)
            .secure(false) // 운영에서는 true
            .path("/")
            .sameSite("Strict")
            .maxAge(1800) // 30분
            .build();

        response.addHeader("Set-Cookie", accessTokenCookie.toString());

        return ResultData.success(1, new LoginResponseDTO(newAccessToken, null));
    }

    /**
     * 로그아웃 처리 API
     * - Redis의 RefreshToken 삭제
     * - AccessToken, RefreshToken 쿠키 삭제
     */
    @PostMapping("/logout")
    public ResultData<String> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshToken(request);
        if (refreshToken == null) {
            return ResultData.fail("RefreshToken이 없습니다. 다시 로그인해주세요.");
        }

        Long accountId;
        try {
            accountId = jwtProvider.parseToken(refreshToken);
        } catch (Exception e) {
            return ResultData.fail("토큰이 유효하지 않습니다. 다시 로그인해주세요.");
        }

        // Redis에서 RefreshToken 삭제
        refreshTokenService.deleteRefreshToken(accountId);

        // 쿠키 삭제 (maxAge=0)
        ResponseCookie deleteAccessTokenCookie = ResponseCookie.from("accessToken", "")
            .httpOnly(true)
            .secure(false) // 운영(prod)에서는 true
            .path("/")
            .sameSite("Strict")
            .maxAge(0)
            .build();

        ResponseCookie deleteRefreshTokenCookie = ResponseCookie.from("refreshToken", "")
            .httpOnly(true)
            .secure(false)
            .path("/")
            .sameSite("Strict")
            .maxAge(0)
            .build();

        response.addHeader("Set-Cookie", deleteAccessTokenCookie.toString());
        response.addHeader("Set-Cookie", deleteRefreshTokenCookie.toString());

        return ResultData.success(1, "로그아웃이 완료되었습니다.");
    }

    /**
     * 요청 쿠키에서 refreshToken 추출
     */
    private String extractRefreshToken(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if ("refreshToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
