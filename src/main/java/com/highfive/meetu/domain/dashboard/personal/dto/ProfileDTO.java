package com.highfive.meetu.domain.dashboard.personal.dto;

import com.highfive.meetu.domain.user.common.entity.Profile;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileDTO {
  private Long id;
  private Integer experienceLevel;
  private Integer educationLevel;
  private String skills;
  private Integer desiredSalaryCode;
  private String profileImageUrl;

  public static ProfileDTO from(Profile profile) {
    return ProfileDTO.builder()
        .id(profile.getId())
        .experienceLevel(profile.getExperienceLevel())
        .educationLevel(profile.getEducationLevel())
        .skills(profile.getSkills())
        .desiredSalaryCode(profile.getDesiredSalaryCode())
        .profileImageUrl(profile.getProfileImageKey())
        .build();
  }
}
