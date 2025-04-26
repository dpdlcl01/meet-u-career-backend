package com.highfive.meetu.domain.resume.personal.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.highfive.meetu.domain.coverletter.common.entity.CoverLetter;
import com.highfive.meetu.domain.resume.common.entity.Resume;
import com.highfive.meetu.domain.user.common.entity.Profile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 이력서 DTO (개인 회원용)
 * - 목록 조회, 상세 조회, 작성, 수정에 공통 사용
 * - 상태/이력서유형 등은 Entity 내 상수를 참조
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumePersonalDTO {

    // 기본 정보 (목록 조회 시 필요)
    private Long id;                    // 이력서 ID
    private String title;               // 이력서 제목
    private LocalDateTime createdAt;    // 생성일
    private LocalDateTime updatedAt;    // 수정일
    private Integer status;             // 상태 코드

    // 목록 조회 화면에서 필요한 추가 정보
    private String overview;            // 간단 소개
    private Integer resumeType;         // 이력서 유형 코드
    private Long coverLetterId;         // 연결된 자기소개서 ID
    private String coverLetterTitle;    // 연결된 자기소개서 제목

    // 이력서 상세정보 (상세 조회/작성/수정 시 필요)
    private String resumeFile;          // 이력서 파일 경로
    private String resumeUrl;           // 이력서 URL
    private String extraLink1;          // 추가 링크 1 (GitHub, 포트폴리오 등)
    private String extraLink2;          // 추가 링크 2 (LinkedIn, 블로그 등)

    // 프로필 정보
    private Long profileId;             // 프로필 ID

    private Boolean isPrimary;  // 대표 이력서 여부

    // 이력서 항목 리스트
    private List<ResumeContentPersonalDTO> contents;


    /**
     * DTO → 엔티티 변환 메서드
     * - 이력서 작성/수정 시 사용
     * - 연관된 프로필(Profile), 자기소개서(CoverLetter)를 인자로 받아 설정
     */
    public Resume toEntity(Profile profile, CoverLetter coverLetter) {
        return Resume.builder()
                .title(this.title)
                .overview(this.overview)
                .resumeType(this.resumeType)
                .extraLink1(this.extraLink1)
                .extraLink2(this.extraLink2)
                .status(this.status != null ? this.status : 1) // 기본값: 임시저장(1)
                .isPrimary(this.isPrimary != null ? this.isPrimary : false)
                .profile(profile)
                .coverLetter(coverLetter)
                .build();
    }

    /**
     * 엔티티 → DTO 변환 메서드
     * - 이력서 상세 조회 시 사용
     * - ResumeContentPersonalDTO 리스트는 사전에 변환된 결과를 전달
     */
    public static ResumePersonalDTO fromEntity(Resume resume, List<ResumeContentPersonalDTO> contentDTOs) {
        return ResumePersonalDTO.builder()
                .id(resume.getId())
                .title(resume.getTitle())
                .overview(resume.getOverview())
                .resumeType(resume.getResumeType())
                .extraLink1(resume.getExtraLink1())
                .extraLink2(resume.getExtraLink2())
                .isPrimary(resume.getIsPrimary())
                .status(resume.getStatus())
                .createdAt(resume.getCreatedAt())
                .updatedAt(resume.getUpdatedAt())
                .profileId(resume.getProfile().getId())
                .coverLetterId(resume.getCoverLetter() != null ? resume.getCoverLetter().getId() : null)
                .coverLetterTitle(resume.getCoverLetter() != null ? resume.getCoverLetter().getTitle() : null)
                .contents(contentDTOs)
                .build();
    }

    // 간단 목록용
    public static ResumePersonalDTO fromEntity(Resume resume) {
        return ResumePersonalDTO.builder()
                .id(resume.getId())
                .title(resume.getTitle())
                .updatedAt(resume.getUpdatedAt())
                .createdAt(resume.getCreatedAt())
                .isPrimary(resume.getIsPrimary())
                .status(resume.getStatus())
                .resumeType(resume.getResumeType())
                .profileId(resume.getProfile().getId())
                .build();
    }

}
