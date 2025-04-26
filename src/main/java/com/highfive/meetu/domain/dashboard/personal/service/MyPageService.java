package com.highfive.meetu.domain.dashboard.personal.service;

import com.highfive.meetu.domain.application.common.repository.ApplicationRepository;
import com.highfive.meetu.domain.company.common.repository.CompanyRepository;
import com.highfive.meetu.domain.dashboard.personal.dto.*;
import com.highfive.meetu.domain.job.common.repository.BookmarkRepository;
import com.highfive.meetu.domain.job.common.repository.JobPostingRepository;
import com.highfive.meetu.domain.resume.common.repository.ResumeRepository;
import com.highfive.meetu.domain.resume.common.repository.ResumeViewLogRepository;
import com.highfive.meetu.domain.user.common.entity.Account;
import com.highfive.meetu.domain.user.common.entity.Profile;
import com.highfive.meetu.domain.user.common.repository.ProfileRepository;
import com.highfive.meetu.global.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPageService {

  private final ProfileRepository profileRepository;
  private final ResumeRepository resumeRepository;
  private final ResumeViewLogRepository resumeViewLogRepository;
  private final ApplicationRepository applicationRepository;
  private final BookmarkRepository bookmarkRepository;
  private final JobPostingRepository jobPostingRepository;
  private final CompanyRepository companyRepository;

  public MyPageDTO getMyPageInfo(Long profileId) {
    Profile profile = profileRepository.findById(profileId)
        .orElseThrow(() -> new NotFoundException("프로필을 찾을 수 없습니다."));
    Account account = profile.getAccount();

    int resumeViewCount = resumeViewLogRepository.countByProfileId(profileId);
    int offerCount = applicationRepository.countOffersByProfileId(profileId); // 필요시 이 메서드 구현
    int bookmarkCount = bookmarkRepository.countByProfile_Id(profileId);

    List<RecentApplicationDTO> recentApplications = applicationRepository.findRecentByProfileId(profileId);
    ApplicationSummaryDTO summary = applicationRepository.aggregateStatusSummary(profileId);
    List<RecommendedJobPostingDTO> recommendedJobs = jobPostingRepository.findRecommendedForProfile(profile, PageRequest.of(0, 6));

    int profileCompleteness = 0;
    if (StringUtils.hasText(account.getName())) profileCompleteness += 20;
    if (StringUtils.hasText(account.getEmail())) profileCompleteness += 20;
    if (StringUtils.hasText(account.getPhone())) profileCompleteness += 20;
    if (account.getBirthday() != null) profileCompleteness += 20;
    if (profile.getLocation() != null && StringUtils.hasText(profile.getLocation().getFullLocation())) profileCompleteness += 20;

    return MyPageDTO.of(
        AccountDTO.from(account),
        ProfileDTO.from(profile),
        resumeViewCount,
        offerCount,
        bookmarkCount,
        recentApplications,
        summary,
        recommendedJobs,
        profileCompleteness
    );
  }
}
