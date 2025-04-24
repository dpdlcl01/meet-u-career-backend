package com.highfive.meetu.domain.job.common.repository;

import com.highfive.meetu.domain.job.common.entity.Bookmark;
import com.highfive.meetu.domain.job.common.entity.JobPosting;
import com.highfive.meetu.domain.user.common.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    /**
     * 특정 공고를 북마크한 사용자 수 조회
     *
     * @param jobPostingId 공고 ID
     * @return 북마크 수
     */
    @Query("""
        SELECT COUNT(b) FROM bookmark b
        WHERE b.jobPosting.id = :jobPostingId
    """)
    int countByJobPostingId(Long jobPostingId);

    /**
     * 사용자가 특정 공고를 북마크했는지 여부 조회
     *
     * @param profile 프로필
     * @param jobPosting 채용공고
     * @return 존재 여부
     */
    boolean existsByProfileAndJobPosting(Profile profile, JobPosting jobPosting);

    /**
     * 사용자가 특정 공고를 북마크한 기록 조회
     *
     * @param profile 프로필
     * @param jobPosting 채용공고
     * @return 북마크 객체 (없으면 Optional.empty())
     */
    Optional<Bookmark> findByProfileAndJobPosting(Profile profile, JobPosting jobPosting);
}
