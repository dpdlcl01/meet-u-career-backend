package com.highfive.meetu.domain.auth.common.dto.findid;

import lombok.*;

/**
 * 사용자 아이디 조회 응답 DTO
 * 조회된 userId를 포함합니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindIdResponseDTO {
  // 조회된 사용자 아이디 (userId)
  private String userId;
}