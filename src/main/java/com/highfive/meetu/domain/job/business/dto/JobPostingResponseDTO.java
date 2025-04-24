package com.highfive.meetu.domain.job.business.dto;

import com.highfive.meetu.domain.job.common.entity.JobPosting;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobPostingResponseDTO {
    private Long id;
    private String title;

    public static JobPostingResponseDTO fromEntity(JobPosting posting) {
        return JobPostingResponseDTO.builder()
            .id(posting.getId())
            .title(posting.getTitle())
            .build();
    }
}
