package com.highfive.meetu.domain.dashboard.personal.dto;

import com.highfive.meetu.domain.user.common.entity.Account;
import com.highfive.meetu.domain.user.common.entity.Profile;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileInfoDTO {
  private Long accountId;
  private String name;
  private String email;
  private String phone;

  private Integer experienceLevel;
  private Integer educationLevel;
  private String skills;
  private Integer desiredSalaryCode;
  private String profileImageUrl;

  public static ProfileInfoDTO build(Account account, Profile profile) {
    return ProfileInfoDTO.builder()
        .accountId(account.getId())
        .name(account.getName())
        .email(account.getEmail())
        .phone(account.getPhone())
        .experienceLevel(profile.getExperienceLevel())
        .educationLevel(profile.getEducationLevel())
        .skills(profile.getSkills())
        .desiredSalaryCode(profile.getDesiredSalaryCode())
        .profileImageUrl(profile.getProfileImageKey())
        .build();
  }
}