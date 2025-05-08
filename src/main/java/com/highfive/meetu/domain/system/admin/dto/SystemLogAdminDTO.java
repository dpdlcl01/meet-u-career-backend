// src/main/java/com/highfive/meetu/domain/system/admin/dto/SystemLogAdminDTO.java
package com.highfive.meetu.domain.system.admin.dto;

import lombok.*;
import java.time.LocalDateTime;

/**
 * 관리자용 시스템 로그 조회 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemLogAdminDTO {

  /** 로그 고유 ID */
  private Long id;

  /** 사용자 식별자 (personal/business 로그인 시 userId, admin 로그인 시 email) */
  private String userId;

  /** 로그 유형 이름 (SECURITY, TRANSACTION, SYSTEM, USER, ERROR) */
  private String type;

  /** 모듈 코드 (0: AUTH, 1: USER, …) */
  private Integer module;

  /** 수행 작업 내용 (예: "login personal") */
  private String action;

  /** 접속 IP 주소 */
  private String ipAddress;

  /** 생성 시각 */
  private LocalDateTime createdAt;
}