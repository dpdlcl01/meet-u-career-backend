package com.highfive.meetu.domain.auth.common.controller.password;

import com.highfive.meetu.domain.auth.common.dto.password.FindPasswordRequestDTO;
import com.highfive.meetu.domain.auth.common.dto.password.FindPasswordResponseDTO;
import com.highfive.meetu.domain.auth.common.dto.password.ResetPasswordRequestDTO;
import com.highfive.meetu.domain.auth.common.dto.password.ResetPasswordResponseDTO;
import com.highfive.meetu.domain.auth.common.dto.password.VerifyCodeRequestDTO;
import com.highfive.meetu.domain.auth.common.dto.password.VerifyCodeResponseDTO;
import com.highfive.meetu.domain.auth.common.service.password.PasswordService;
import com.highfive.meetu.global.common.response.ResultData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 비밀번호 재설정 관련 API 엔드포인트
 */
@RestController
@RequestMapping("/api/password")
@RequiredArgsConstructor
public class PasswordController {

  private final PasswordService passwordService;

  /**
   * 1단계: 사용자 정보 확인 및 인증 코드 발송
   * @param dto email, name, birthday 포함
   * @return 인증 코드 전송 결과 및 검증용 토큰 or 소셜 로그인 정보
   */
  @PostMapping("/find")
  public ResultData<FindPasswordResponseDTO> findPassword(@RequestBody FindPasswordRequestDTO dto) {
    FindPasswordResponseDTO response = passwordService.requestPasswordReset(dto);
    return ResultData.success(1, response);
  }

  /**
   * 2단계: 인증 코드 검증
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
   * @param dto verificationToken, code, newPassword 포함
   * @return 재설정 성공 여부
   */
  @PutMapping("/reset")
  public ResultData<ResetPasswordResponseDTO> resetPassword(@RequestBody ResetPasswordRequestDTO dto) {
    ResetPasswordResponseDTO response = passwordService.resetPassword(dto);
    return ResultData.success(1, response);
  }
}
