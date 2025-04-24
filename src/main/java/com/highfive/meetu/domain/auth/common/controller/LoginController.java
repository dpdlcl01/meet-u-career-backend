package com.highfive.meetu.domain.auth.common.controller;

import com.highfive.meetu.domain.auth.personal.dto.LoginRequestDTO;
import com.highfive.meetu.domain.auth.personal.dto.LoginResponseDTO;
import com.highfive.meetu.domain.auth.common.service.login.LoginService;
import com.highfive.meetu.global.common.response.ResultData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 로그인(Login) 전용 API 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LoginController {

    private final LoginService loginService;

    /**
     * 개인 로그인 요청
     * - AccessToken, RefreshToken 발급
     */
    @PostMapping("/personal/auth/login")
    public ResponseEntity<ResultData<LoginResponseDTO>> personalLogin(@RequestBody LoginRequestDTO dto) {
        return loginService.userLogin(dto, 0);
    }


  /**
   * 기업 로그인 요청
   * - AccessToken, RefreshToken 발급
   */
  @PostMapping("/business/auth/login")
  public ResponseEntity<ResultData<LoginResponseDTO>> businessLogin(@RequestBody LoginRequestDTO dto) {
    return loginService.userLogin(dto, 1);
  }


    /**
     * 관리자 로그인 요청
     * - AccessToken, RefreshToken 발급
     */
    @PostMapping("/admin/auth/login")
    public ResponseEntity<ResultData<LoginResponseDTO>> adminLogin(@RequestBody LoginRequestDTO dto) {
        return loginService.adminLogin(dto);
    }
}
