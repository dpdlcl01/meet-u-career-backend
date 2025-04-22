package com.highfive.meetu.domain.job.common.repository;

import com.highfive.meetu.domain.job.common.entity.JobPosting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JobPosting (채용 공고) Repository
 * - 인기 공고, 최신 공고, 지원자 많은 공고 등 조회
 * - 추천 공고 조회
 * - 기업별 공고 조회
 */
@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

    /**
     * 인기 공고 조회 (조회수 기준 내림차순)
     */
    @Query("""
        SELECT j FROM jobPosting j
        WHERE j.status = 2
        ORDER BY j.viewCount DESC
    """)
    Page<JobPosting> findPopular(Pageable pageable);

    /**
     * 최신 공고 조회 (등록일 기준 내림차순)
     */
    @Query("""
        SELECT j FROM jobPosting j
        WHERE j.status = 2
        ORDER BY j.createdAt DESC
    """)
    Page<JobPosting> findLatest(Pageable pageable);

    /**
     * 지원자 수 많은 공고 조회 (지원자 수 기준 내림차순)
     */
    @Query("""
        SELECT j FROM jobPosting j
        WHERE j.status = 2
        ORDER BY j.applyCount DESC
    """)
    Page<JobPosting> findMostApplied(Pageable pageable);

    /**
     * 키워드를 기반으로 추천 공고 조회 (최대 3개 키워드)
     */
    @Query("""
        SELECT DISTINCT j FROM jobPosting j
        WHERE j.status = 2
        AND (
            LOWER(j.keyword) LIKE LOWER(CONCAT('%', :keyword1, '%'))
            OR LOWER(j.keyword) LIKE LOWER(CONCAT('%', :keyword2, '%'))
            OR LOWER(j.keyword) LIKE LOWER(CONCAT('%', :keyword3, '%'))
        )
        ORDER BY j.createdAt DESC
    """)
    Page<JobPosting> findRecommended(
        @Param("keyword1") String keyword1,
        @Param("keyword2") String keyword2,
        @Param("keyword3") String keyword3,
        Pageable pageable
    );

    /**
     * 외부 고유 ID (jobId)로 공고 조회
     */
    Optional<JobPosting> findByJobId(String jobId);

    /**
     * 특정 기업(companyId)의 전체 채용 공고 조회 (등록일 내림차순)
     */
    @Query("""
        SELECT j FROM jobPosting j
        WHERE j.company.id = :companyId
        ORDER BY j.createdAt DESC
    """)
    List<JobPosting> findAllByCompanyId(@Param("companyId") Long companyId);
}
