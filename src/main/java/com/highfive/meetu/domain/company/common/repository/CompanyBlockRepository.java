package com.highfive.meetu.domain.company.common.repository;

import com.highfive.meetu.domain.company.common.entity.CompanyBlock;
import com.highfive.meetu.domain.company.common.entity.Company;
import com.highfive.meetu.domain.user.common.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * [Repository] 기업 차단(CompanyBlock) 관리
 * - 개인회원이 차단한 기업 정보를 관리
 */
@Repository
public interface CompanyBlockRepository extends JpaRepository<CompanyBlock, Long> {

    /**
     * 특정 프로필이 차단한 모든 기업 목록 조회
     *
     * @param profileId 프로필 ID
     * @return 차단된 CompanyBlock 리스트
     */
    @Query("SELECT c FROM companyBlock c WHERE c.profile.id = :profileId")
    List<CompanyBlock> findAllByProfileId(@Param("profileId") Long profileId);

    /**
     * 특정 프로필이 특정 기업을 차단했는지 여부 확인
     *
     * @param profile 프로필 엔티티
     * @param company 기업 엔티티
     * @return 존재 여부 (true/false)
     */
    boolean existsByProfileAndCompany(Profile profile, Company company);
}
