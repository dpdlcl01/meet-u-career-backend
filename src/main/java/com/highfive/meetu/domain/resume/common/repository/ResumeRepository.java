package com.highfive.meetu.domain.resume.common.repository;

import com.highfive.meetu.domain.resume.common.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {

    /**
     * 특정 프로필의 상태가 ACTIVE(0)인 이력서를 수정일 기준으로 내림차순 조회
     *
     * @param profileId 프로필 ID
     * @param status 이력서 상태 (0: 활성, 1: 임시저장, 2: 삭제)
     * @return 조건에 맞는 이력서 리스트
     */
    List<Resume> findAllByProfileIdAndStatusOrderByUpdatedAtDesc(Long profileId, Integer status);


    /**
     * 이력서와 이력서 항목(contents)을 함께 조회
     *
     * JPQL:
     * SELECT r FROM resume r JOIN FETCH r.resumeContentList WHERE r.id = :resumeId
     *
     * SQL (MyBatis 등에서 사용하는 전통적 SQL):
     * SELECT *
     * FROM resume r
     * LEFT JOIN resume_content c ON r.id = c.resume_id
     * WHERE r.id = ?
     */
    @Query("SELECT r FROM resume r JOIN FETCH r.resumeContentList WHERE r.id = :resumeId")
    Optional<Resume> findWithContentsById(@Param("resumeId") Long resumeId);



    // ----------------------------------
    @Modifying
    @Query("UPDATE resume r SET r.isPrimary = false WHERE r.profile.id = :profileId AND r.status <> 3")
    void clearPrimaryResume(@Param("profileId") Long profileId);



}
