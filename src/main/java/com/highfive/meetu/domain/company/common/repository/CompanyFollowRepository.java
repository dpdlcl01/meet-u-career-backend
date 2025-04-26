package com.highfive.meetu.domain.company.common.repository;

import com.highfive.meetu.domain.company.common.entity.Company;
import com.highfive.meetu.domain.company.common.entity.CompanyFollow;
import com.highfive.meetu.domain.user.common.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import java.util.Optional;

@Repository
public interface CompanyFollowRepository extends JpaRepository<CompanyFollow, Long> {

    List<CompanyFollow> findByProfile_Account_Id(Long accountId);

    /**
     * 특정 회사에 대해 팔로우한 사용자의 수 조회
     *
     * @param companyId 회사 ID
     * @return 팔로우 수
     */
    @Query("""
        SELECT COUNT(cf) FROM companyFollow cf
        WHERE cf.company.id = :companyId
    """)
    int countByCompanyId(Long companyId);

    /**
     * 사용자가 특정 회사를 팔로우했는지 여부 조회
     *
     * @param profile 프로필
     * @param company 회사
     * @return 존재 여부
     */
    boolean existsByProfileAndCompany(Profile profile, Company company);

    /**
     * 사용자가 특정 회사를 팔로우한 기록 조회
     *
     * @param profile 프로필
     * @param company 회사
     * @return 관심 등록 객체 (없으면 Optional.empty())
     */
    Optional<CompanyFollow> findByProfileAndCompany(Profile profile, Company company);
}
