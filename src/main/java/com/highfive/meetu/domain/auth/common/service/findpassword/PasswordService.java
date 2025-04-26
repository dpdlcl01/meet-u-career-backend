package com.highfive.meetu.domain.auth.common.service.findpassword;

import com.highfive.meetu.domain.auth.common.dto.findpassword.FindPasswordRequestDTO;
import com.highfive.meetu.domain.auth.common.dto.findpassword.FindPasswordResponseDTO;
import com.highfive.meetu.domain.auth.common.dto.findpassword.ResetPasswordRequestDTO;
import com.highfive.meetu.domain.auth.common.dto.findpassword.ResetPasswordResponseDTO;
import com.highfive.meetu.domain.auth.common.dto.findpassword.VerifyCodeRequestDTO;
import com.highfive.meetu.domain.auth.common.dto.findpassword.VerifyCodeResponseDTO;

/**
 * 비밀번호 재설정 요청 → 코드 발송 → 검증 → 재설정 전체 흐름을 담당합니다.
 */
public interface PasswordService {

  /**
   * 1단계: 이메일·이름·생년월일 일치 확인 후
   *    - OAuth 계정이면 socialProvider 리턴
   *    - 일반 계정이면 인증 코드 발급 후 이메일 전송, JWT 토큰 리턴
   */
  FindPasswordResponseDTO requestPasswordReset(FindPasswordRequestDTO dto);

  /**
   * 2단계: 토큰+코드 검증
   */
  VerifyCodeResponseDTO verifyCode(VerifyCodeRequestDTO dto);

  /**
   * 3단계: 토큰+코드 재검증 후 새 비밀번호로 업데이트
   */
  ResetPasswordResponseDTO resetPassword(ResetPasswordRequestDTO dto);

}
