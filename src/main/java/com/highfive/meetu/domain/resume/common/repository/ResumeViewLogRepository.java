package com.highfive.meetu.domain.resume.common.repository;

import com.highfive.meetu.domain.resume.common.entity.ResumeViewLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeViewLogRepository extends JpaRepository<ResumeViewLog, Long> {

  @Query("SELECT COUNT(rvl) " +
      "FROM resumeViewLog rvl " +
      "JOIN rvl.resume res " +
      "JOIN res.profile p " +
      "WHERE p.id = :profileId")
  int countByProfileId(@Param("profileId") Long profileId);

  int countByResumeId(Long resumeId);
}
