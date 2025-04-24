package com.highfive.meetu.domain.auth.common.dto.findpassword;

import lombok.*;

/**
 * 비밀번호 찾기 응답 DTO
 * 인증 코드 전송 여부 및 검증용 토큰을 반환합니다.
 * OAuth 계정일 경우 socialProvider와 email 정보만 포함됩니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindPasswordResponseDTO {
  // 인증 코드 전송 성공 여부
  private boolean sent;
  // 코드 검증용 JWT 토큰
  private String verificationToken;
  // OAuth 제공자명 (예: GOOGLE), 일반 계정일 땐 null
  private String socialProvider;
  // OAuth 계정의 이메일 (일반 계정일 땐 null)
  private String email;
}
