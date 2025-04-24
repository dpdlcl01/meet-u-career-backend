package com.highfive.meetu.domain.auth.common.controller;

import com.highfive.meetu.domain.auth.common.dto.findpassword.FindPasswordRequestDTO;
import com.highfive.meetu.domain.auth.common.dto.findpassword.FindPasswordResponseDTO;
import com.highfive.meetu.domain.auth.common.dto.findpassword.ResetPasswordRequestDTO;
import com.highfive.meetu.domain.auth.common.dto.findpassword.ResetPasswordResponseDTO;
import com.highfive.meetu.domain.auth.common.dto.findpassword.VerifyCodeRequestDTO;
import com.highfive.meetu.domain.auth.common.dto.findpassword.VerifyCodeResponseDTO;
import com.highfive.meetu.domain.auth.common.service.findpassword.PasswordService;
import com.highfive.meetu.global.common.response.ResultData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 비밀번호 재설정 관련 API 엔드포인트
 */
@RestController
@RequestMapping("/api/password")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000") // <-- Add this line!
public class FindPasswordController {

  private final PasswordService passwordService;

  /**
   * 1단계: 사용자 정보 확인 및 인증 코드 발송
   * 
   * @param dto email, name, userId 포함
   * @return 인증 코드 전송 결과 및 검증용 토큰 or 소셜 로그인 정보
   */
  @PostMapping("/find")
  public ResultData<FindPasswordResponseDTO> findPassword(@RequestBody FindPasswordRequestDTO dto) {
    FindPasswordResponseDTO response = passwordService.requestPasswordReset(dto);
    return ResultData.success(1, response);
  }

  /**
   * 2단계: 인증 코드 검증
   * 
   * @param dto verificationToken, code 포함
   * @return 검증 성공 여부
   */
  @PostMapping("/verify")
  public ResultData<VerifyCodeResponseDTO> verifyCode(@RequestBody VerifyCodeRequestDTO dto) {
    VerifyCodeResponseDTO response = passwordService.verifyCode(dto);
    return ResultData.success(1, response);
  }

  /**
   * 3단계: 인증 코드 재검증 후 비밀번호 재설정
   * 
   * @param dto verificationToken, code, newPassword 포함
   * @return 재설정 성공 여부
   */
  @PutMapping("/reset")
  public ResultData<ResetPasswordResponseDTO> resetPassword(@RequestBody ResetPasswordRequestDTO dto) {
    ResetPasswordResponseDTO response = passwordService.resetPassword(dto);
    return ResultData.success(1, response);
  }
}
