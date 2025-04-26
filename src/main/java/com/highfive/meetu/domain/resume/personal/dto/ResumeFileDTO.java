package com.highfive.meetu.domain.resume.personal.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeFileDTO {
    private Long profileId;          // FK: 프로필 ID
    private String title;            // 이력서 제목
    private Integer status;          // 공개 여부 (1: 비공개, 2: 공개)
}

