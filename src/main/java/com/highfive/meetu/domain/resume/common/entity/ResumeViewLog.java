package com.highfive.meetu.domain.resume.common.entity;

import com.highfive.meetu.domain.application.common.entity.Application;
import com.highfive.meetu.domain.company.common.entity.Company;
import com.highfive.meetu.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * 이력서 열람 로그 엔티티
 *
 * 연관관계:
 * - Resume(1) : ResumeViewLog(N) - ResumeViewLog가 주인, @JoinColumn 사용
 * - Company(1) : ResumeViewLog(N) - ResumeViewLog가 주인, @JoinColumn 사용
 * - Application(1) : ResumeViewLog(N) - ResumeViewLog가 주인, @JoinColumn 사용
 */
@Entity(name = "resumeViewLog")
@Table(
        indexes = {
                @Index(name = "idx_resume_view_resumeId", columnList = "resumeId"),
                @Index(name = "idx_resume_view_companyId", columnList = "companyId"),
                @Index(name = "idx_resume_view_createdAt", columnList = "createdAt")
        }
)
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"resume", "company", "application"})
public class ResumeViewLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resumeId", nullable = false)
    private Resume resume;  // 열람된 이력서

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "companyId", nullable = false)
    private Company company;  // 열람한 기업

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicationId")
    private Application application;  // 해당 열람이 특정 지원서와 연관된 경우

    @Column(nullable = false)
    private Integer viewType;  // 열람 유형 (GENERAL, EVALUATION)

    @Column(nullable = false)
    private Boolean isNotified;  // 열람 알림 여부


    /**
     * 열람 유형
     */
    public static class ViewType {
        public static final int GENERAL = 0;    // 일반 열람
        public static final int EVALUATION = 1; // 평가를 위한 열람
    }
}