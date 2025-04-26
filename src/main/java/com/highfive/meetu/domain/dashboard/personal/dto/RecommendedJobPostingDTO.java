package com.highfive.meetu.domain.dashboard.personal.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class RecommendedJobPostingDTO {

  private String companyName;       // 회사명
  private String jobTitle;          // 공고 제목
  private String location;          // 전체 지역명
  private String salaryRange;       // 연봉 범위 텍스트
  private LocalDateTime deadline;   // 공고 마감일 ← 기존 LocalDate에서 수정
  private String preferredSkills;   // 선호 키워드

  // 🔧 JPQL에서 사용하는 정확한 생성자 추가 (타입 순서 일치)
  public RecommendedJobPostingDTO(String companyName, String jobTitle, String location,
                                  String salaryRange, LocalDateTime deadline, String preferredSkills) {
    this.companyName = companyName;
    this.jobTitle = jobTitle;
    this.location = location;
    this.salaryRange = salaryRange;
    this.deadline = deadline;
    this.preferredSkills = preferredSkills;
  }
}
