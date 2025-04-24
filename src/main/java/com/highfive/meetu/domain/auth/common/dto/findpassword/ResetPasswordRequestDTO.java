package com.highfive.meetu.domain.auth.common.dto.findpassword;

import lombok.*;

/**
 * 비밀번호 재설정 요청 DTO
 * 토큰+코드 검증 후 새로운 비밀번호를 설정합니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResetPasswordRequestDTO {
  // 인증 코드 검증용 JWT 토큰
  private String verificationToken;
  // 이메일로 전송된 6자리 인증 코드
  private String code;
  // 새 비밀번호 (평문)
  private String newPassword;
}
