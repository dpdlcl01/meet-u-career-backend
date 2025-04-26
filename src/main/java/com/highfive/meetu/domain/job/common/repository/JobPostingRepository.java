package com.highfive.meetu.domain.job.common.repository;

import com.highfive.meetu.domain.dashboard.personal.dto.RecommendedJobPostingDTO;
import com.highfive.meetu.domain.job.common.entity.JobPosting;
import com.highfive.meetu.domain.user.common.entity.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import java.util.List;

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
  @Query("SELECT new com.highfive.meetu.domain.dashboard.personal.dto.RecommendedJobPostingDTO(" +
      "c.name, jp.title, l.fullLocation, jp.salaryRange, jp.expirationDate, jp.keyword) " +
      "FROM jobPosting jp " +
      "JOIN jp.company c " +
      "JOIN jp.location l " +
      "WHERE jp.status = 2 " +
      "ORDER BY jp.expirationDate ASC")
  List<RecommendedJobPostingDTO> findRecommendedForProfile(@Param("profile") Profile profile, org.springframework.data.domain.Pageable pageable);




    /**
     * 활성 상태인 공고 중 마감일이 가까운 순으로 10개 조회 (비회원용)
     */
    List<JobPosting> findTop10ByExpirationDateAfterAndStatusOrderByExpirationDateAsc(
            LocalDateTime now, int status
    );

    List<JobPosting> findByCompany_IdInAndStatus(List<Long> companyIds, int status);

    // 특정 기업회원이 속한 기업이 등록한 공고
    List<JobPosting> findByCompany_IdAndStatus(Long companyId, Integer status);

    /**
     * 인기 공고 조회 (조회수 기준 내림차순)
     *
     * @param pageable 페이징 정보
     * @return 인기 공고 목록
     */
    @Query("""
        SELECT j FROM jobPosting j
        WHERE j.status = 2
        ORDER BY j.viewCount DESC
    """)
    Page<JobPosting> findPopular(Pageable pageable);

    /**
     * 최신 공고 조회 (등록일 기준 내림차순)
     *
     * @param pageable 페이징 정보
     * @return 최신 공고 목록
     */
    @Query("""
        SELECT j FROM jobPosting j
        WHERE j.status = 2
        ORDER BY j.createdAt DESC
    """)
    Page<JobPosting> findLatest(Pageable pageable);

    /**
     * 지원자 수 기준으로 인기 공고 조회 (지원자 수 내림차순)
     *
     * @param pageable 페이징 정보
     * @return 지원 많은 공고 목록
     */
    @Query("""
        SELECT j FROM jobPosting j
        WHERE j.status = 2
        ORDER BY j.applyCount DESC
    """)
    Page<JobPosting> findMostApplied(Pageable pageable);

    /**
     * 키워드를 기반으로 추천 공고 조회 (최대 3개 키워드)
     *
     */
    @Query("""
        SELECT DISTINCT j FROM jobPosting j
        WHERE j.status = 2 AND (
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
     *
     * @param jobId 외부 시스템 연동용 공고 ID
     * @return 채용 공고 Optional
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

    /**
     * 기업 ID와 공고 상태를 기반으로 해당 기업의 공고 개수 조회
     *
     * @param companyId 기업 ID
     * @param status 공고 상태 코드 (ex: 2 = 활성화)
     * @return 공고 수
     */
    @Query("""
        SELECT COUNT(j)
        FROM jobPosting j
        WHERE j.company.id = :companyId
          AND j.status = :status
    """)
    int countByCompanyIdAndStatus(@Param("companyId") Long companyId, @Param("status") int status);

    /**
     * 공고 ID와 상태로 공고 조회
     *
     * @param id 공고 ID
     * @param status 공고 상태
     * @return 채용 공고 Optional
     */
    Optional<JobPosting> findByIdAndStatus(Long id, int status);
}
