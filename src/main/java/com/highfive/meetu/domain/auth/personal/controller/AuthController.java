package com.highfive.meetu.domain.auth.personal.controller;

import com.highfive.meetu.domain.auth.personal.dto.LoginResponseDTO;
import com.highfive.meetu.domain.auth.personal.service.RefreshTokenService;
import com.highfive.meetu.global.common.response.ResultData;
import com.highfive.meetu.infra.jwt.JwtProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import static com.highfive.meetu.global.util.CookieUtil.extractRefreshToken;

/**
 * 인증(Authentication) 관련 API 컨트롤러
 * - AccessToken 재발급
 * - 로그아웃 처리 포함
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/personal/auth")
public class AuthController {

    /**
     * secure 플래그
     */
    @Value("${cookie.secure:false}")
    private boolean cookieSecure;

    private final RefreshTokenService refreshTokenService;

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
            .secure(cookieSecure)
            .path("/")
            .sameSite("Strict")
            .maxAge(1800) // 30분
            .build();

        response.addHeader("Set-Cookie", accessTokenCookie.toString());

        return ResultData.success(1, new LoginResponseDTO(newAccessToken, null));
    }
}
