package com.highfive.meetu.domain.auth.common.service.findpassword;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * JWT 페이로드로부터 추출한 비밀번호 재설정 정보
 */
@Getter
@AllArgsConstructor
public class PasswordResetPayload {
  // 토큰의 subject에 담긴 이메일
  private final String email;
  // 토큰의 claim에 담긴 6자리 인증 코드
  private final String code;
}
