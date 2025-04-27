package com.highfive.meetu.domain.coverletter.personal.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FitAnalysisRequestDTO {
    private Long contentId;        // 자기소개서 항목 ID
    private Long jobCategoryId;    // 직무 ID
    private String content;        // 항목 내용
    private String jobTitle;       // 직무명
    private String jobPostingText; // 공고문 내용 (nullable)
}
