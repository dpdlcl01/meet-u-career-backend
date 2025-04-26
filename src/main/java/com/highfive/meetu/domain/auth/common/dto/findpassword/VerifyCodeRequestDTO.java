package com.highfive.meetu.domain.auth.common.dto.findpassword;

import lombok.*;

/**
 * 인증 코드 검증 요청 DTO
 * 클라이언트에서 받은 토큰과 코드를 비교합니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyCodeRequestDTO {
  // 이전 단계에서 발급받은 JWT 토큰
  private String verificationToken;
  // 이메일로 전송된 6자리 인증 코드
  private String code;
}
