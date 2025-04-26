package com.highfive.meetu.domain.auth.common.controller;

import com.highfive.meetu.domain.auth.personal.dto.LoginRequestDTO;
import com.highfive.meetu.domain.auth.personal.dto.LoginResponseDTO;
import com.highfive.meetu.domain.auth.common.service.login.LoginService;
import com.highfive.meetu.global.common.response.ResultData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 로그인(Login) 전용 API 컨트롤러
 */
@Tag(name = "00-login-controller", description = "로그인/토큰 발급 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LoginController {

    private final LoginService loginService;

    /**
     * 개인 로그인 요청
     * - AccessToken, RefreshToken 발급
     */
    @Operation(summary = "개인회원 로그인 API")
    @PostMapping("/personal/auth/login")
    public ResponseEntity<ResultData<LoginResponseDTO>> personalLogin(@RequestBody LoginRequestDTO dto) {
        return loginService.userLogin(dto);
    }

    /**
     * 기업 로그인 요청
     * - AccessToken, RefreshToken 발급
     */
    @Operation(summary = "기업회원 로그인 API")
    @PostMapping("/business/auth/login")
    public ResponseEntity<ResultData<LoginResponseDTO>> businessLogin(@RequestBody LoginRequestDTO dto) {
        return loginService.businessLogin(dto);
    }

    /**
     * 관리자 로그인 요청
     * - AccessToken, RefreshToken 발급
     */
    @Operation(summary = "관리자 로그인 API")
    @PostMapping("/admin/auth/login")
    public ResponseEntity<ResultData<LoginResponseDTO>> adminLogin(@RequestBody LoginRequestDTO dto) {
        return loginService.adminLogin(dto);
    }
}
