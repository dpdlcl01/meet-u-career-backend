package com.highfive.meetu.domain.resume.common.repository;

import com.highfive.meetu.domain.resume.common.entity.ResumeContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeContentRepository extends JpaRepository<ResumeContent, Long> {


    void deleteAllByResumeId(Long resumeId);

    @Modifying
    @Query("DELETE FROM resumeContent rc WHERE rc.resume.id = :resumeId")
    void deleteByResumeId(@Param("resumeId") Long resumeId);


}
