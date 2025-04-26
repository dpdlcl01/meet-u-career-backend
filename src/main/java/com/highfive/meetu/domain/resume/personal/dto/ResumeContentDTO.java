package com.highfive.meetu.domain.resume.personal.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResumeContentDTO {

    private Long id;
    private Long resumeId;             // FK
    private Integer sectionType;       // 섹션 타입 (0~3: 학력/경력/자격/활동 등)
    private String sectionTitle;       // 사용자 커스텀 항목 제목
    private String organization;       // 기관/학교/회사 등
    private String title;              // 직책/학위/자격증명 등
    private String field;              // 전공/직무/분야
    private LocalDate dateFrom;        // 시작일 (yyyy-MM-dd)
    private LocalDate dateTo;          // 종료일 (yyyy-MM-dd)
    private String description;        // 상세 설명 (Rich Text 가능)

    private String contentFileKey;     // 첨부파일 key (S3)
    private String contentFileName;    // 원본 파일명
    private String contentFileType;    // MIME 타입
    private Integer contentOrder;      // 항목 순서
}
