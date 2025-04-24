package com.highfive.meetu.domain.application.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.highfive.meetu.domain.job.common.entity.JobPosting;
import com.highfive.meetu.domain.resume.common.entity.Resume;
import com.highfive.meetu.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
/*
    * 연관관계:
   * - Application(N) : Profile(1) - Application이 주인, @JoinColumn 사용
   * - Application(N) : JobPosting(1) - Application이 주인, @JoinColumn 사용
 */
import com.highfive.meetu.domain.user.common.entity.Profile;

/**
 * 지원서 엔티티
 * - Application(N) : Resume(1) - Application이 주인, @JoinColumn 사용
 */
@Entity(name = "application")
@Table(indexes = {
        @Index(name = "idx_application_profileId", columnList = "profileId"),
        @Index(name = "idx_application_jobPostingId", columnList = "jobPostingId"),
        @Index(name = "idx_application_resumeId", columnList = "resumeId"),
        @Index(name = "idx_application_status", columnList = "status")
})
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = { "profile", "jobPosting", "resume" })
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Application extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profileId", nullable = false)
    private Profile profile; // 지원자의 프로필 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jobPostingId", nullable = false)
    private JobPosting jobPosting; // 지원한 채용 공고 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resumeId", nullable = false)
    private Resume resume; // 제출한 이력서 ID

    @Column(nullable = false)
    private Integer status; // 지원 상태 (PENDING, REVIEWING, INTERVIEWING, REJECTED, CANCELED)

    // 상태 코드 상수 정의
    public static class Status {
        public static final int APPLIED = 0;
        public static final int DOCUMENT_PASSED = 1;
        public static final int DOCUMENT_REJECTED = 2;
        public static final int INTERVIEW_COMPLETED = 3;
        public static final int DELETED = 4;
    }

}