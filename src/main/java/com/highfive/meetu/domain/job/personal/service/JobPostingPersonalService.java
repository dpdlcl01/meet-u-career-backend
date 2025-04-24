package com.highfive.meetu.domain.job.personal.service;

import com.highfive.meetu.domain.application.common.repository.ApplicationRepository;
import com.highfive.meetu.domain.application.common.repository.InterviewReviewRepository;
import com.highfive.meetu.domain.company.common.repository.CompanyFollowRepository;
import com.highfive.meetu.domain.job.common.repository.ApplicationQueryPersonalRepository;
import com.highfive.meetu.domain.job.common.repository.BookmarkRepository;
import com.highfive.meetu.domain.job.common.entity.JobPosting;
import com.highfive.meetu.domain.job.common.repository.JobPostingRepository;
import com.highfive.meetu.domain.job.personal.dto.JobPostingDetailDTO;
import com.highfive.meetu.domain.user.common.entity.Profile;
import com.highfive.meetu.domain.user.common.repository.ProfileRepository;
import com.highfive.meetu.global.common.exception.NotFoundException;
import com.highfive.meetu.global.common.response.ResultData;
import com.highfive.meetu.infra.oauth.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 개인 회원 채용 공고 상세 조회 서비스
 * - 로그인 여부에 따라 데이터 추가 제공 (스크랩 여부, 관심 기업 여부 등)
 */
@Service
@RequiredArgsConstructor
public class JobPostingPersonalService {

    private final JobPostingRepository jobPostingRepository;
    private final CompanyFollowRepository companyFollowRepository;
    private final BookmarkRepository bookmarkRepository;
    private final ApplicationRepository applicationRepository;
    private final ProfileRepository profileRepository;
    private final InterviewReviewRepository interviewReviewRepository;
    private final ApplicationQueryPersonalRepository applicationQueryRepository;

    /**
     * 채용 공고 상세 조회
     * - 로그인 여부에 따라 스크랩 여부, 지원 여부, 관심기업 여부 포함
     *
     * @param jobPostingId 조회할 공고 ID
     * @return ResultData로 래핑된 JobPostingDetailDTO
     */
    @Transactional(readOnly = true)
    public ResultData<JobPostingDetailDTO> getJobPostingDetail(Long jobPostingId) {
        // 1. 공고 기본 정보 조회 (활성화된 공고만)
        JobPosting jobPosting = jobPostingRepository.findByIdAndStatus(jobPostingId, JobPosting.Status.ACTIVE)
            .orElseThrow(() -> new NotFoundException("존재하지 않거나 비활성화된 공고입니다."));

        // 2. 로그인 여부에 따라 개인 프로필 및 상태 체크
        boolean isAuthenticated = SecurityUtil.isAuthenticated();
        boolean isCompanyFollowed = false;
        boolean isBookmarked = false;
        boolean isApplied = false;

        Profile profile = null;

        if (isAuthenticated) {
            Long accountId = SecurityUtil.getAccountId();
            profile = profileRepository.findByAccountId(accountId)
                .orElseThrow(() -> new NotFoundException("사용자 프로필을 찾을 수 없습니다."));

            isCompanyFollowed = companyFollowRepository.existsByProfileAndCompany(profile, jobPosting.getCompany());
            isBookmarked = bookmarkRepository.existsByProfileAndJobPosting(profile, jobPosting);
            isApplied = applicationRepository.existsByProfileAndJobPosting(profile, jobPosting);
        }

        // 3. 지원자 통계 정보 조회
        int totalApplicants = applicationQueryRepository.countApplicantsByJobPostingId(jobPostingId);
        int newApplicants = applicationQueryRepository.countNewApplicants(jobPostingId);
        int experiencedApplicants = applicationQueryRepository.countExperiencedApplicants(jobPostingId);

        JobPostingDetailDTO.ApplicantStats.EducationStats educationStats = JobPostingDetailDTO.ApplicantStats.EducationStats.builder()
            .highSchoolCount(applicationQueryRepository.countApplicantsByEducationLevel(jobPostingId, 1))
            .collegeCount(applicationQueryRepository.countApplicantsByEducationLevel(jobPostingId, 2))
            .universityCount(applicationQueryRepository.countApplicantsByEducationLevel(jobPostingId, 3))
            .masterCount(applicationQueryRepository.countApplicantsByEducationLevel(jobPostingId, 4))
            .doctorCount(applicationQueryRepository.countApplicantsByEducationLevel(jobPostingId, 5))
            .etcCount(0) // 기타 학력 카운트는 필요 시 추가 가능
            .build();

        JobPostingDetailDTO.ApplicantStats.SalaryStats salaryStats = JobPostingDetailDTO.ApplicantStats.SalaryStats.builder()
            .below4000Count(applicationQueryRepository.countSalaryBelow4000(jobPostingId))
            .range4000to6000Count(applicationQueryRepository.countSalaryRange4000to6000(jobPostingId))
            .range6000to8000Count(applicationQueryRepository.countSalaryRange6000to8000(jobPostingId))
            .above8000Count(applicationQueryRepository.countSalaryAbove8000(jobPostingId))
            .negotiableCount(applicationQueryRepository.countNegotiableSalary(jobPostingId))
            .build();

        // 4. 기업/공고 관련 통계 조회
        int bookmarkCount = bookmarkRepository.countByJobPostingId(jobPostingId); // 공고 스크랩 수
        int companyFollowCount = companyFollowRepository.countByCompanyId(jobPosting.getCompany().getId()); // 회사 팔로우 수
        int openJobPostingCount = jobPostingRepository.countByCompanyIdAndStatus(jobPosting.getCompany().getId(), JobPosting.Status.ACTIVE); // 현재 열려있는 공고 수
        int interviewReviewCount = interviewReviewRepository.countByCompanyId(jobPosting.getCompany().getId()); // 인터뷰 후기 수

        // 5. DTO 변환 및 결과 반환
        JobPostingDetailDTO dto = JobPostingDetailDTO.from(
            jobPosting,
            bookmarkCount,
            companyFollowCount,
            openJobPostingCount,
            interviewReviewCount,
            isCompanyFollowed,
            isBookmarked,
            isApplied,
            totalApplicants,
            JobPostingDetailDTO.ApplicantStats.ExperienceStats.builder()
                .newApplicantCount(newApplicants)
                .experiencedApplicantCount(experiencedApplicants)
                .build(),
            educationStats,
            salaryStats
        );

        return ResultData.success(1, dto);
    }
}
