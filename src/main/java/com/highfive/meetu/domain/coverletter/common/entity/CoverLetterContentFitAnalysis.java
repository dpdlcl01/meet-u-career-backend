package com.highfive.meetu.domain.coverletter.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.highfive.meetu.domain.coverletter.common.entity.CoverLetterContent;
import com.highfive.meetu.domain.job.common.entity.JobCategory;
import com.highfive.meetu.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * 자기소개서 직무 적합도 분석 결과 엔티티
 *
 * 연관관계:
 * - CoverLetterContentFitAnalysis(N) : CoverLetterContent(1)
 * - CoverLetterContentFitAnalysis(N) : JobCategory(1)
 */
@Entity(name = "coverLetterContentFitAnalysis")
@Table(
        indexes = {
                @Index(name = "idx_coverLetterContentFitAnalysis_contentId", columnList = "contentId"),
                @Index(name = "idx_coverLetterContentFitAnalysis_jobCategoryId", columnList = "jobCategoryId")
        }
)
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"content", "jobCategory"})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CoverLetterContentFitAnalysis extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contentId", nullable = false)
    private CoverLetterContent content;  // 자기소개서 항목 (FK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jobCategoryId", nullable = false)
    private JobCategory jobCategory;  // 직무 카테고리 (FK)

    @Column(nullable = false)
    private Double fitScore;  // 적합도 점수 (0~100)

    @Column(length = 1000, nullable = false)
    private String comment;  // 분석 요약 코멘트
}
