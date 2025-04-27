package com.highfive.meetu.domain.coverletter.personal.dto;

import com.highfive.meetu.domain.coverletter.common.entity.CoverLetter;
import com.highfive.meetu.domain.coverletter.common.entity.CoverLetterContent;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoverLetterPersonalViewDTO {
    private Long id;
    private String title;
    private LocalDateTime updatedAt;
    private List<SectionDTO> sections;

    public static CoverLetterPersonalViewDTO fromEntity(CoverLetter entity) {
        return CoverLetterPersonalViewDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .updatedAt(entity.getUpdatedAt())
                .sections(entity.getCoverLetterContentList() != null
                        ? entity.getCoverLetterContentList().stream()
                        .map(SectionDTO::fromEntity)
                        .toList()
                        : null)
                .build();
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SectionDTO {
        private Long id;
        private String sectionTitle;
        private String content;
        private Integer contentOrder;

        public static SectionDTO fromEntity(CoverLetterContent content) {
            return SectionDTO.builder()
                    .id(content.getId())
                    .sectionTitle(content.getSectionTitle())
                    .content(content.getContent())
                    .contentOrder(content.getContentOrder())
                    .build();
        }
    }
}
