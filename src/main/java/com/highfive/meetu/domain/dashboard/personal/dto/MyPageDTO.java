package com.highfive.meetu.domain.dashboard.personal.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MyPageDTO {
  private AccountDTO account;
  private ProfileDTO profile;
  private int resumeViewCount;
  private int offerCount;
  private int bookmarkCount;
  private List<RecentApplicationDTO> recentApplications;
  private ApplicationSummaryDTO summary;
  private List<RecommendedJobPostingDTO> recommendedJobs;
  private int profileCompleteness; // 추가된 필드

  public static MyPageDTO of(
      AccountDTO account,
      ProfileDTO profile,
      int resumeViewCount,
      int offerCount,
      int bookmarkCount,
      List<RecentApplicationDTO> recentApplications,
      ApplicationSummaryDTO summary,
      List<RecommendedJobPostingDTO> recommendedJobs,
      int profileCompleteness // 추가된 파라미터
  ) {
    return MyPageDTO.builder()
        .account(account)
        .profile(profile)
        .resumeViewCount(resumeViewCount)
        .offerCount(offerCount)
        .bookmarkCount(bookmarkCount)
        .recentApplications(recentApplications)
        .summary(summary)
        .recommendedJobs(recommendedJobs)
        .profileCompleteness(profileCompleteness)
        .build();
  }
}
