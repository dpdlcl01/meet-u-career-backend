package com.highfive.meetu.domain.auth.common.dto.findpassword;

import lombok.*;

/**
 * 인증 코드 검증 응답 DTO
 * 코드 일치 여부만 반환합니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyCodeResponseDTO {
  // 코드 검증 성공 여부
  private boolean verified;
}
