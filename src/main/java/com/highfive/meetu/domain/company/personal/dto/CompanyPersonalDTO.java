// src/main/java/com/highfive/meetu/domain/company/personal/dto/CompanyPersonalDTO.java
package com.highfive.meetu.domain.company.personal.dto;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyPersonalDTO {
  // Sidebar 에 필요한 정보
  private String logoKey;       // 기업 로고 S3 키
  private String name;          // 회사명
  private String companyType;   // 기업 형태 (대기업, 벤처 등)
  private String industry;      // 업종
  private String location;      // 위치 (address 필드 매핑)
  private LocalDate foundedDate;// 설립일
  private Integer numEmployees; // 직원 수

  // Company Introduction 에 필요한 정보 (위 필드 재사용)
  // foundedDate, companyType, numEmployees
}