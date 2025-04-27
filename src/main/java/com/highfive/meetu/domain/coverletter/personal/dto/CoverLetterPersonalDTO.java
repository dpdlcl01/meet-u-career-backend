package com.highfive.meetu.domain.coverletter.personal.dto;

import com.highfive.meetu.domain.coverletter.common.entity.CoverLetter;
import com.highfive.meetu.domain.coverletter.common.entity.CoverLetterContent;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 개인 사용자의 자기소개서 DTO
 * - 작성/수정/조회/목록 화면 공통으로 사용
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoverLetterPersonalDTO {

    private Long id; // 자기소개서 ID
    private Long profileId; // 프로필 ID
    private String title; // 자기소개서 제목
    private LocalDateTime updatedAt; // 수정일
    private Integer contentCount; // 항목 개수
    private List<CoverLetterContentDTO> contents; // 자기소개서 항목 리스트

    /**
     * Entity → DTO 변환 메서드
     */
    public static CoverLetterPersonalDTO fromEntity(CoverLetter entity) {
        return CoverLetterPersonalDTO.builder()
                .id(entity.getId())
                .profileId(entity.getProfile().getId())
                .title(entity.getTitle())
                .updatedAt(entity.getUpdatedAt())
                .contentCount(entity.getCoverLetterContentList() != null
                        ? entity.getCoverLetterContentList().size()
                        : 0)
                .contents(entity.getCoverLetterContentList() != null
                        ? entity.getCoverLetterContentList().stream()
                        .map(CoverLetterContentDTO::fromEntity)
                        .collect(Collectors.toList())
                        : null)
                .build();
    }

    /**
     * 자기소개서 항목 DTO (내부 static class)
     */
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CoverLetterContentDTO {
        private Long id;
        private String sectionTitle;
        private String content;
        private Integer contentOrder;

        public static CoverLetterContentDTO fromEntity(CoverLetterContent entity) {
            return CoverLetterContentDTO.builder()
                    .id(entity.getId())
                    .sectionTitle(entity.getSectionTitle())
                    .content(entity.getContent())
                    .contentOrder(entity.getContentOrder())
                    .build();
        }

        public CoverLetterContent toEntity(CoverLetter coverLetter) {
            return CoverLetterContent.builder()
                    .coverLetter(coverLetter)
                    .sectionTitle(this.sectionTitle)
                    .content(this.content)
                    .contentOrder(this.contentOrder)
                    .build();
        }
    }

}
