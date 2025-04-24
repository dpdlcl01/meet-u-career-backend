package com.highfive.meetu.domain.auth.common.dto.findpassword;

import lombok.*;

/**
 * 비밀번호 재설정 응답 DTO
 * 재설정 성공 여부만 반환합니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResetPasswordResponseDTO {
  // 비밀번호 재설정 성공 여부
  private boolean reset;
}

