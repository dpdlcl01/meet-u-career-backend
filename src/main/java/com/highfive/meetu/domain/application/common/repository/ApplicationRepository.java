package com.highfive.meetu.domain.application.common.repository;

import com.highfive.meetu.domain.application.common.entity.Application;
import com.highfive.meetu.domain.dashboard.personal.dto.ApplicationSummaryDTO;
import com.highfive.meetu.domain.dashboard.personal.dto.RecentApplicationDTO;
import com.highfive.meetu.domain.job.common.entity.JobPosting;
import com.highfive.meetu.domain.user.common.entity.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long>, ApplicationRepositoryCustom {

  @Query("SELECT COUNT(a) FROM application a WHERE a.profile.id = :profileId")
  int countOffersByProfileId(@Param("profileId") Long profileId);

  @Query("""
        SELECT a
        FROM application a
        JOIN FETCH a.profile p
        WHERE a.jobPosting.id = :jobPostingId
          AND a.status != 4
    """)
  List<Application> findAllByJobPostingId(@Param("jobPostingId") Long jobPostingId);

  boolean existsByProfileAndJobPosting(Profile profile, JobPosting jobPosting);

  @Query("SELECT new com.highfive.meetu.domain.dashboard.personal.dto.RecentApplicationDTO(" +
          "c.name, jp.title, a.status) " +
          "FROM application a " +
          "JOIN a.jobPosting jp " +
          "JOIN jp.company c " +
          "WHERE a.profile.id = :profileId " +
          "ORDER BY a.createdAt DESC")
  List<RecentApplicationDTO> findRecentByProfileId(@Param("profileId") Long profileId);

  // ✅ 수정됨: status 0 ~ 4 포함하는 요약 쿼리
  @Query("SELECT new com.highfive.meetu.domain.dashboard.personal.dto.ApplicationSummaryDTO(" +
          "SUM(CASE WHEN a.status = 0 THEN 1 ELSE 0 END), " + // waiting
          "SUM(CASE WHEN a.status = 1 THEN 1 ELSE 0 END), " + // passedDocument
          "SUM(CASE WHEN a.status = 2 THEN 1 ELSE 0 END), " + // interview1st
          "SUM(CASE WHEN a.status = 3 THEN 1 ELSE 0 END), " + // finalAccepted
          "SUM(CASE WHEN a.status = 4 THEN 1 ELSE 0 END)) " + // rejected
          "FROM application a " +
          "WHERE a.profile.id = :profileId")
  ApplicationSummaryDTO aggregateStatusSummary(@Param("profileId") Long profileId);

  @Query("""
        SELECT COUNT(a.id)
        FROM application a
        WHERE a.status = 0 AND a.createdAt < :start
    """)
  long countTotalApplicationsPrevious(@Param("start") LocalDateTime start);

  @Query("""
        SELECT COUNT(a.id)
        FROM application a
        WHERE a.status = 1 AND a.createdAt < :start
    """)
  long countAcceptedApplicationsPrevious(@Param("start") LocalDateTime start);

  @Query("""
        SELECT COUNT(a.id)
        FROM application a
        WHERE a.status = 2 AND a.createdAt < :start
    """)
  long countRejectedApplicationsPrevious(@Param("start") LocalDateTime start);

  @Query("""
        SELECT COUNT(a.id)
        FROM application a
        WHERE a.status = 0
    """)
  long countTotalApplications();

  @Query("""
        SELECT COUNT(a.id)
        FROM application a
        WHERE a.status = 1
    """)
  long countAcceptedApplications();

  @Query("""
        SELECT COUNT(a.id)
        FROM application a
        WHERE a.status = 2
    """)
  long countRejectedApplications();

  @Query(value = """
        SELECT 
            CASE 
                WHEN TIMESTAMPDIFF(YEAR, acc.birthday, CURRENT_DATE()) BETWEEN 10 AND 19 THEN '10대'
                WHEN TIMESTAMPDIFF(YEAR, acc.birthday, CURRENT_DATE()) BETWEEN 20 AND 29 THEN '20대'
                WHEN TIMESTAMPDIFF(YEAR, acc.birthday, CURRENT_DATE()) BETWEEN 30 AND 39 THEN '30대'
                WHEN TIMESTAMPDIFF(YEAR, acc.birthday, CURRENT_DATE()) BETWEEN 40 AND 49 THEN '40대'
                WHEN TIMESTAMPDIFF(YEAR, acc.birthday, CURRENT_DATE()) BETWEEN 50 AND 59 THEN '50대'
                WHEN TIMESTAMPDIFF(YEAR, acc.birthday, CURRENT_DATE()) >= 60 THEN '60대 이상'
                ELSE '기타'
            END AS ageGroup,
            COUNT(a.id)
        FROM application a
        JOIN profile p ON a.profileId = p.id
        JOIN account acc ON p.accountId = acc.id
        GROUP BY ageGroup
        ORDER BY FIELD(ageGroup, '10대', '20대', '30대', '40대', '50대', '60대 이상', '기타')
    """, nativeQuery = true)
  List<Object[]> countApplicantsByAgeGroup();

  @Query("""
        SELECT c.name, COUNT(a.id)
        FROM application a
        JOIN a.jobPosting j
        JOIN j.company c
        GROUP BY c.name
        ORDER BY COUNT(a.id) DESC
    """)
  List<Object[]> countTopCompaniesByApplicants(Pageable pageable);

  @Query(value = """
        SELECT 
            CONCAT(LPAD(HOUR(createdAt), 2, '0'), '시') AS hourSlot,
            COUNT(*) AS applied
        FROM application
        WHERE status = 0
        GROUP BY hourSlot
        ORDER BY hourSlot
    """, nativeQuery = true)
  List<Object[]> findApplicationTimeStatsHourly();

  @Query("""
        SELECT 
            DATE(a.createdAt) AS date,
            COUNT(a.id) AS totalApplications,
            SUM(CASE WHEN a.status = 1 THEN 1 ELSE 0 END) AS acceptedApplications,
            SUM(CASE WHEN a.status = 2 THEN 1 ELSE 0 END) AS rejectedApplications
        FROM application a
        WHERE a.createdAt >= :start AND a.createdAt <= :end AND a.status != 4
        GROUP BY DATE(a.createdAt)
        ORDER BY DATE(a.createdAt)
    """)
  List<Object[]> findDailyApplicationStats(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

  List<Application> findByProfile_Account_Id(Long accountId);

  @Query(value = """
        SELECT COUNT(*) 
        FROM application a
        JOIN jobPosting jp ON a.jobPostingId = jp.id
        WHERE jp.companyId = :companyId
    """, nativeQuery = true)
  int countTotalApplicationsByCompanyId(@Param("companyId") Long companyId);

  @Query("""
        SELECT a.jobPosting.id, COUNT(a)
        FROM application a
        WHERE a.jobPosting.company.id = :companyId
        GROUP BY a.jobPosting.id
    """)
  List<Object[]> countApplicationsByJobPostingGrouped(@Param("companyId") Long companyId);

  default Map<Long, Integer> countApplicationsGroupByJobPosting(Long companyId) {
    List<Object[]> results = countApplicationsByJobPostingGrouped(companyId);
    Map<Long, Integer> map = new HashMap<>();
    for (Object[] row : results) {
      map.put((Long) row[0], ((Long) row[1]).intValue());
    }
    return map;
  }

  int countByJobPostingId(Long jobPostingId);

  int countByJobPostingIdAndStatus(Long jobPostingId, int status);

  @Query("SELECT COUNT(a) FROM application a WHERE a.profile.id = :profileId")
  int countApplicationsByProfileId(@Param("profileId") Long profileId);
}
