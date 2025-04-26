package com.highfive.meetu.domain.resume.common.repository;

import com.highfive.meetu.domain.resume.common.entity.ResumeContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeContentRepository extends JpaRepository<ResumeContent, Long> {
    void deleteAllByResumeId(Long resumeId);

    @Query("SELECT rc.field FROM resumeContent rc JOIN rc.resume r WHERE r.profile.id = :userId GROUP BY rc.field ORDER BY COUNT(rc.id) DESC")
    List<String> findTop3KeywordsByUserId(@Param("userId") Long userId);
}
