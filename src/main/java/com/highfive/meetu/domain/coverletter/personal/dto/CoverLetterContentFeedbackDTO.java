package com.highfive.meetu.domain.coverletter.personal.dto;

import com.highfive.meetu.domain.coverletter.common.entity.CoverLetterContentFeedback;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 자기소개서 항목별 AI 피드백 DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoverLetterContentFeedbackDTO {

    private Long id; // 피드백 ID
    private String originalContent; // 요청 시점 원본 내용
    private String feedback; // 1~2문장 피드백 요약
    private String revisedContent; // AI가 수정한 전체 문장
    private Boolean isApplied; // 적용 여부
    private LocalDateTime appliedAt; // 적용 시각
    private LocalDateTime createdAt; // 피드백 생성일

    public static CoverLetterContentFeedbackDTO fromEntity(CoverLetterContentFeedback entity) {
        return CoverLetterContentFeedbackDTO.builder()
                .id(entity.getId())
                .originalContent(entity.getOriginalContent())
                .feedback(entity.getFeedback())
                .revisedContent(entity.getRevisedContent())
                .isApplied(entity.getIsApplied())
                .appliedAt(entity.getAppliedAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
