package com.highfive.meetu.domain.application.common.repository;

import com.highfive.meetu.domain.application.common.entity.Application;
import com.highfive.meetu.domain.dashboard.personal.dto.RecentApplicationDTO;
import com.highfive.meetu.domain.dashboard.personal.dto.ApplicationSummaryDTO;
import com.highfive.meetu.domain.job.common.entity.JobPosting;
import com.highfive.meetu.domain.user.common.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long>, ApplicationRepositoryCustom {

  @Query("SELECT COUNT(a) FROM application a WHERE a.profile.id = :profileId")
  int countOffersByProfileId(@Param("profileId") Long profileId);
  /**
   * 특정 공고에 지원한 이력 전체 조회 (상태 4 제외)
   */
  @Query("""
        SELECT a
        FROM application a
        JOIN FETCH a.profile p
        WHERE a.jobPosting.id = :jobPostingId
          AND a.status != 4
    """)
  List<Application> findAllByJobPostingId(@Param("jobPostingId") Long jobPostingId);

  /**
   * 지원 이력 존재 여부 확인
   */
  boolean existsByProfileAndJobPosting(Profile profile, JobPosting jobPosting);

  /**
   * 마이페이지에서 최근 지원 이력 조회 (기업명, 제목, 상태)
   */
  @Query("SELECT new com.highfive.meetu.domain.dashboard.personal.dto.RecentApplicationDTO(" +
      "c.name, jp.title, a.status) " +
      "FROM application a " +
      "JOIN a.jobPosting jp " +
      "JOIN jp.company c " +
      "WHERE a.profile.id = :profileId " +
      "ORDER BY a.createdAt DESC")
  List<RecentApplicationDTO> findRecentByProfileId(@Param("profileId") Long profileId);

  /**
   * 마이페이지 상태 요약 (status = 1~4)
   */
  @Query("SELECT new com.highfive.meetu.domain.dashboard.personal.dto.ApplicationSummaryDTO(" +
      "SUM(CASE WHEN a.status = 1 THEN 1 ELSE 0 END), " +
      "SUM(CASE WHEN a.status = 2 THEN 1 ELSE 0 END), " +
      "SUM(CASE WHEN a.status = 3 THEN 1 ELSE 0 END), " +
      "SUM(CASE WHEN a.status = 4 THEN 1 ELSE 0 END)) " +
      "FROM application a " +
      "WHERE a.profile.id = :profileId")
  ApplicationSummaryDTO aggregateStatusSummary(@Param("profileId") Long profileId);

    // 일정 관리 기능 관련
    List<Application> findByProfile_Account_Id(Long accountId);
}
