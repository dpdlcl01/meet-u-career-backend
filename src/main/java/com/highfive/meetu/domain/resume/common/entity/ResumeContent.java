package com.highfive.meetu.domain.resume.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.highfive.meetu.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

/**
 * 이력서 항목 엔티티
 *
 * 연관관계:
 * - ResumeContent(N) : Resume(1) - ResumeContent가 주인, @JoinColumn 사용
 */
@Entity(name = "resumeContent")
@Table(
        indexes = {
                @Index(name = "idx_resumeContent_resumeId", columnList = "resumeId"),
                @Index(name = "idx_resumeContent_sectionType", columnList = "sectionType"),
                @Index(name = "idx_resumeContent_contentOrder", columnList = "contentOrder")
        }
)
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"resume"})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ResumeContent extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resumeId", nullable = false)
    private Resume resume;  // 해당 이력서 ID (외래 키)

    @Column(nullable = false)
    private Integer sectionType;  // 항목 유형 (EDUCATION, EXPERIENCE, CERTIFICATE, ACTIVITY)

    @Column(length = 255, nullable = false)
    private String sectionTitle;  // 항목 제목 ("경력", "학력" 등)

    @Column(nullable = false)
    private Integer contentOrder;  // 항목 순서 (낮을수록 상단 표시)

    @Column(length = 255, nullable = false)
    private String organization;  // 기관/기업명/발급기관 (예: "서울대학교", "삼성전자", "한국산업인력공단")

    @Column(length = 255, nullable = false)
    private String title;  // 직책, 학위명 또는 자격증명 (예: "백엔드 개발자", "정보처리기사")

    @Column(length = 255, nullable = false)
    private String field;  // 전공, 직무 분야 (예: "컴퓨터공학"), 관련 분야 (예: "IT", "금융", "교육")

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;  // 상세 설명

    @Column
    private LocalDate dateFrom;  // 시작일 또는 발급일 (자격증일 경우 이 컬럼 사용)

    @Column
    private LocalDate dateTo;  // 종료일 (자격증이면 NULL)

    @Column(length = 500)
    private String contentFileKey; // S3 Key

    @Column(length = 255)
    private String contentFileName; // 업로드 당시 원본 파일명

    @Column(length = 100)
    private String contentFileType; // MIME 타입 (pdf, word 등)


    /**
     * 항목 구분
     */
    public static class SectionType {
        public static final int EDUCATION = 0;       // 학력 사항
        public static final int EXPERIENCE = 1;      // 경력 사항
        public static final int CERTIFICATION = 2;   // 외국어/자격증/수상
        public static final int ACTIVITY = 3;        // 활동/경험/교육
        public static final int PORTFOLIO = 4;       // 포트폴리오
        public static final int COVER_LETTER = 5;    // 자기소개서
        public static final int CUSTOM = 6;          // 사용자 정의 항목
    }


}