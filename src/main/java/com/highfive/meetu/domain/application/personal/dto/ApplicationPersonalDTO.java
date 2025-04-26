package com.highfive.meetu.domain.application.personal.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 개인 회원용 지원 내역 조회 DTO
 * - Application + JobPosting + (회사명) 등 필요한 필드 매핑
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationPersonalDTO {

  // Application 기본 정보
  private Long applicationId; // 지원서 PK
  private Long resumeId; // 제출한 이력서 ID
  private String resumeTitle; // 제출한 이력서 제목
  private Integer status; // 지원 상태
  private LocalDateTime appliedAt; // 지원 일자 (Application.createdAt)

  // JobPosting 연관 정보
  private Long jobPostingId;
  private String jobTitle;
  private LocalDateTime expirationDate;

  // 회사명
  private String companyName;

}
