package com.highfive.meetu.domain.resume.personal.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResumeDTO {

    private Long id;
    private Long profileId;           // FK
    private String title;             // 이력서 제목
    private String overview;          // 간단 소개
    private Integer resumeType;       // 유형 (0: 직접입력, 1: 파일, 2: URL)
    private String resumeFileKey;     // S3 key (파일형)
    private String resumeFileName;    // 원본 파일명
    private String resumeFileType;    // 파일 MIME 타입
    private String resumeUrl;         // URL형 이력서 링크
    private String extraLink1;        // 외부 링크 1
    private String extraLink2;        // 외부 링크 2
    private Integer status;           // 공개/비공개 상태
}
