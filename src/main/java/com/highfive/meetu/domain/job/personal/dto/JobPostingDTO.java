package com.highfive.meetu.domain.job.personal.dto;

import com.highfive.meetu.domain.job.common.entity.JobPosting;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPostingDTO {
    private Long id;
    private String title;
    private String companyName;
    private String industry;
    private String jobType;
    private String salaryRange;
    private String locationCode;
    private Integer viewCount;
    private Integer applyCount;
    private LocalDateTime expirationDate;

    public static JobPostingDTO from(JobPosting entity) {
        return JobPostingDTO.builder()
            .id(entity.getId())
            .title(entity.getTitle())
            .companyName(entity.getCompany().getName())
            .industry(entity.getIndustry())
            .jobType(entity.getJobType())
            .salaryRange(entity.getSalaryRange())
            .locationCode(entity.getLocation().getLocationCode())
            .viewCount(entity.getViewCount())
            .applyCount(entity.getApplyCount())
            .expirationDate(entity.getExpirationDate())
            .build();
    }
}
