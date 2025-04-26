package com.highfive.meetu.domain.resume.personal.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeBasicInfoDTO {

    // Resume 관련
    private String title;
    private String overview;
    private Integer resumeType;
    private String resumeUrl;
    private String extraLink1;
    private String extraLink2;
    private Integer status;      // DRAFT, PUBLIC 등
    private Boolean isPrimary;

    // Profile 관련
    private Long profileId;
    private Integer experienceLevel;
    private Integer educationLevel;
    private Integer desiredSalaryCode;
    private String skills;
    private Long desiredJobCategoryId;
    private Long locationId;
}

