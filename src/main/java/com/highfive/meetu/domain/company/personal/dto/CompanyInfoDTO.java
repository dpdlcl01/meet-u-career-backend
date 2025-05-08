// src/main/java/com/highfive/meetu/domain/company/personal/dto/CompanyInfoDTO.java
package com.highfive.meetu.domain.company.personal.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Company의 모든 정보를 프론트엔드로 전송하기 위한 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyInfoDTO {

  // --- BaseEntity 공통 필드 ---
  private Long id;                     // 회사 고유 ID
  private LocalDateTime createdAt;     // 생성일시
  private LocalDateTime updatedAt;     // 수정일시

  // --- Company 엔티티 필드 ---
  private String name;                 // 회사명
  private String businessNumber;       // 사업자등록번호
  private String representativeName;   // 대표자명
  private String industry;             // 업종
  private LocalDate foundedDate;       // 설립일
  private Integer numEmployees;        // 직원 수
  private Long revenue;                // 매출액
  private String website;              // 웹사이트 URL
  private String logoKey;              // 로고 S3 키
  private String address;              // 회사 주소
  private String companyType;          // 기업 형태
  private String corpCode;             // API 연동용 코드
  private Long operatingProfit;        // 영업이익
  private Integer status;              // 상태 (0: ACTIVE, 1: INACTIVE)
  private Long avgAnnualSalary;        // 평균연봉
}
