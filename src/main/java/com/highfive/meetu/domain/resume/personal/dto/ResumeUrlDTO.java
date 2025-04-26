package com.highfive.meetu.domain.resume.personal.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeUrlDTO {
    private Long profileId;
    private String title;
    private String resumeUrl;
}

