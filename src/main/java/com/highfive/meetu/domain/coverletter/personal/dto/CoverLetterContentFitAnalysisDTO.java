package com.highfive.meetu.domain.coverletter.personal.dto;

import com.highfive.meetu.domain.coverletter.common.entity.CoverLetterContentFitAnalysis;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 자기소개서 항목별 직무 적합도 분석 DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoverLetterContentFitAnalysisDTO {

    private Long id; // 분석 결과 ID
    private String jobTitle; // 분석 대상 직무명
    private Double fitScore; // 적합도 점수 (0~100)
    private String comment; // 분석 요약 코멘트
    private LocalDateTime createdAt; // 분석 생성일

    public static CoverLetterContentFitAnalysisDTO fromEntity(CoverLetterContentFitAnalysis entity) {
        return CoverLetterContentFitAnalysisDTO.builder()
                .id(entity.getId())
                .jobTitle(entity.getJobCategory().getJobName())
                .fitScore(entity.getFitScore())
                .comment(entity.getComment())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
