package com.highfive.meetu.domain.auth.common.dto.findid;

import lombok.*;

/**
 * 사용자 아이디 조회 요청 DTO
 * 사용자 이름과 이메일을 입력받습니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindIdRequestDTO {
  // 사용자 이름
  private String name;
  // 사용자 이메일
  private String email;
}