package com.highfive.meetu.domain.dashboard.personal.service;

import com.highfive.meetu.domain.application.common.repository.ApplicationRepository;
import com.highfive.meetu.domain.dashboard.personal.dto.*;
import com.highfive.meetu.domain.job.common.repository.BookmarkRepository;
import com.highfive.meetu.domain.job.common.repository.JobPostingRepository;
import com.highfive.meetu.domain.offer.common.repository.OfferRepository;
import com.highfive.meetu.domain.resume.common.repository.ResumeViewLogRepository;
import com.highfive.meetu.domain.user.common.entity.Account;
import com.highfive.meetu.domain.user.common.entity.Profile;
import com.highfive.meetu.domain.user.common.repository.ProfileRepository;
import com.highfive.meetu.global.common.exception.NotFoundException;
import com.highfive.meetu.infra.oauth.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final ProfileRepository profileRepository;
    private final ResumeViewLogRepository resumeViewLogRepository;
    private final ApplicationRepository applicationRepository;
    private final BookmarkRepository bookmarkRepository;
    private final JobPostingRepository jobPostingRepository;
    private final OfferRepository offerRepository;

    public MyPageDTO getMyPageInfo() {
        Long profileId = SecurityUtil.getProfileId();
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new NotFoundException("프로필을 찾을 수 없습니다."));
        Account account = profile.getAccount();

        int resumeViewCount = resumeViewLogRepository.countByProfileId(profileId);
        int offerCount = offerRepository.countByPersonalAccountId(account.getId());
        int bookmarkCount = bookmarkRepository.countByProfile_Id(profileId);
        int applicationCount = applicationRepository.countApplicationsByProfileId(profileId); // ✅ 핵심 추가

        List<RecentApplicationDTO> recentApplications = applicationRepository.findRecentByProfileId(profileId);
        ApplicationSummaryDTO summary = applicationRepository.aggregateStatusSummary(profileId);
        List<RecommendedJobPostingDTO> recommendedJobs = jobPostingRepository.findRecommendedForProfile(profile, PageRequest.of(0, 6));

        int profileCompleteness = 0;
        if (StringUtils.hasText(account.getName())) profileCompleteness += 14;
        if (StringUtils.hasText(account.getEmail())) profileCompleteness += 14;
        if (StringUtils.hasText(account.getPhone())) profileCompleteness += 14;
        if (profile.getEducationLevel() != null) profileCompleteness += 14;
        if (profile.getExperienceLevel() != null) profileCompleteness += 14;
        if (profile.getDesiredSalaryCode() != null) profileCompleteness += 14;
        if (profile.getSkills() != null) profileCompleteness += 16;

        return MyPageDTO.of(
                AccountDTO.from(account),
                ProfileDTO.from(profile),
                resumeViewCount,
                offerCount,
                bookmarkCount,
                applicationCount, // ✅ 여기도 전달
                recentApplications,
                summary,
                recommendedJobs,
                profileCompleteness
        );
    }
}
