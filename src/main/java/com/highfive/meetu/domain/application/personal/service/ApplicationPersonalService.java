package com.highfive.meetu.domain.application.personal.service;

import com.highfive.meetu.domain.application.common.entity.Application;
import com.highfive.meetu.domain.application.common.repository.ApplicationQueryRepository;
import com.highfive.meetu.domain.application.common.repository.ApplicationRepository;
import com.highfive.meetu.domain.application.personal.dto.ApplicationPersonalDTO;
import com.highfive.meetu.domain.job.common.entity.JobPosting;
import com.highfive.meetu.domain.job.common.repository.JobPostingRepository;
import com.highfive.meetu.domain.resume.common.entity.Resume;
import com.highfive.meetu.domain.resume.common.repository.ResumeRepository;
import com.highfive.meetu.domain.user.common.entity.Profile;
import com.highfive.meetu.domain.user.common.repository.ProfileRepository;
import com.highfive.meetu.global.common.exception.BadRequestException;
import com.highfive.meetu.global.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 개인 회원용 Application(지원서) Service
 */
@Service
@RequiredArgsConstructor
public class ApplicationPersonalService {

    private final ApplicationQueryRepository applicationQueryRepository;
    private final ApplicationRepository applicationRepository;
    private final ProfileRepository profileRepository;
    private final JobPostingRepository jobPostingRepository;
    private final ResumeRepository resumeRepository;

    /**
     * 특정 profileId의 지원 내역 목록 조회
     */
    public List<ApplicationPersonalDTO> getMyApplications(Long profileId) {
        // profileId가 실제 존재하는지 먼저 확인 (선택적)
        // 굳이 필요 없다면 생략 가능
        /*
         * boolean existsProfile = profileRepository.existsById(profileId);
         * if(!existsProfile) {
         * throw new NotFoundException("해당 프로필을 찾을 수 없습니다.");
         * }
         */

        // QueryDSL 통해 조회
        return applicationQueryRepository
            .findAllByProfileId(profileId)
            .stream()
            .filter(dto -> dto.getStatus() != Application.Status.DELETED)
            .toList();
    }

    /**
     * 특정 profileId와 status에 따른 지원 내역 목록 조회
     */
    public List<ApplicationPersonalDTO> getApplicationsByProfileIdAndStatus(Long profileId, Integer status) {
        return applicationQueryRepository.findAllByProfileId(profileId)
            .stream()
            .filter(dto -> (status == null || dto.getStatus().equals(status)))
            .filter(dto -> dto.getStatus() != Application.Status.DELETED)
            .toList();
    }

    /**
     * 지원 내역 취소
     */
    @Transactional
    public void cancelApplication(Long applicationId, Long profileId) {
        var app = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new NotFoundException("지원 내역이 존재하지 않습니다."));

        if (!app.getProfile().getId().equals(profileId)) {
            throw new NotFoundException("해당 사용자의 지원이 아닙니다.");
        }

        app.setStatus(Application.Status.DELETED);
        applicationRepository.save(app);
    }

    /**
     * 입사 지원 (지원서 제출)
     * @param profileId 지원할 사용자 프로필 ID
     * @param jobPostingId 지원할 채용 공고 ID
     * @param resumeId 제출할 이력서 ID
     */
    @Transactional
    public void applyForJob(Long profileId, Long jobPostingId, Long resumeId) {
        // 프로필, 채용 공고, 이력서 조회
        Profile profile = profileRepository.findById(profileId)
            .orElseThrow(() -> new NotFoundException("사용자 프로필을 찾을 수 없습니다."));
        JobPosting jobPosting = jobPostingRepository.findById(jobPostingId)
            .orElseThrow(() -> new NotFoundException("채용 공고를 찾을 수 없습니다."));
        Resume resume = resumeRepository.findById(resumeId)
            .orElseThrow(() -> new NotFoundException("이력서를 찾을 수 없습니다."));

        // 이미 지원한 경우 예외 처리
        boolean alreadyApplied = applicationRepository.existsByProfileAndJobPosting(profile, jobPosting);
        if (alreadyApplied) {
            throw new BadRequestException("이미 지원한 채용 공고입니다.");
        }

        // 지원서 생성
        Application application = Application.builder()
            .profile(profile)
            .jobPosting(jobPosting)
            .resume(resume)
            .status(Application.Status.APPLIED)  // 상태: 지원 완료
            .build();

        // 지원서 저장
        applicationRepository.save(application);
    }
}
