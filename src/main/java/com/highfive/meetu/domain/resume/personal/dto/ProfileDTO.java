package com.highfive.meetu.domain.resume.personal.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileDTO {

    private Long id;
    private Long accountId;            // FK
    private Long locationId;           // 지역 코드
    private String skills;             // 보유 기술 (예: "Java, Spring, Docker")
    private Integer educationLevel;         // 학력 수준 코드 (예: 3 → 대졸)
    private Integer experienceLevel;        // 경력 수준 코드 (예: 1 → 신입)
    private Long desiredJobCategoryId;      // 희망 직무 ID
    private Long desiredLocationId;         // 희망 지역 ID
    private Integer desiredSalaryCode;      // 희망 연봉 코드

    private String profileImageKey;    // S3 저장 키
}
