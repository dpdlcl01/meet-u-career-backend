package com.highfive.meetu.domain.job.personal.service;

import com.highfive.meetu.domain.job.common.entity.Bookmark;
import com.highfive.meetu.domain.job.common.entity.JobPosting;
import com.highfive.meetu.domain.job.common.repository.BookmarkRepository;
import com.highfive.meetu.domain.job.common.repository.JobPostingRepository;
import com.highfive.meetu.domain.user.common.entity.Profile;
import com.highfive.meetu.domain.user.common.repository.ProfileRepository;
import com.highfive.meetu.global.common.exception.NotFoundException;
import com.highfive.meetu.infra.oauth.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final ProfileRepository profileRepository;
    private final JobPostingRepository jobPostingRepository;

    /**
     * 채용공고 스크랩 등록
     * - 현재 로그인한 사용자의 프로필을 조회하여 스크랩 등록
     * - 이미 등록되어 있다면 예외 발생
     *
     * @param jobPostingId 스크랩할 공고 ID
     */
    @Transactional
    public void bookmark(Long jobPostingId) {
        Long profileId = SecurityUtil.getProfileId();

        Profile profile = profileRepository.findById(profileId)
            .orElseThrow(() -> new NotFoundException("사용자 프로필을 찾을 수 없습니다."));
        JobPosting jobPosting = jobPostingRepository.findById(jobPostingId)
            .orElseThrow(() -> new NotFoundException("채용 공고를 찾을 수 없습니다."));

        if (bookmarkRepository.existsByProfileAndJobPosting(profile, jobPosting)) {
            throw new IllegalStateException("이미 스크랩한 공고입니다.");
        }

        Bookmark bookmark = Bookmark.builder()
            .profile(profile)
            .jobPosting(jobPosting)
            .build();

        bookmarkRepository.save(bookmark);
    }

    /**
     * 채용공고 스크랩 해제
     * - 현재 로그인한 사용자의 프로필을 조회하여 스크랩 해제
     * - 등록된 스크랩이 없다면 예외 발생
     *
     * @param jobPostingId 스크랩 해제할 공고 ID
     */
    @Transactional
    public void unbookmark(Long jobPostingId) {
        Long profileId = SecurityUtil.getProfileId();

        Profile profile = profileRepository.findById(profileId)
            .orElseThrow(() -> new NotFoundException("사용자 프로필을 찾을 수 없습니다."));
        JobPosting jobPosting = jobPostingRepository.findById(jobPostingId)
            .orElseThrow(() -> new NotFoundException("채용 공고를 찾을 수 없습니다."));

        Bookmark bookmark = bookmarkRepository.findByProfileAndJobPosting(profile, jobPosting)
            .orElseThrow(() -> new IllegalStateException("스크랩한 공고가 없습니다."));

        bookmarkRepository.delete(bookmark);
    }
}
