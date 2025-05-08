package com.highfive.meetu.domain.company.common.repository;

import com.highfive.meetu.domain.application.personal.dto.InterviewCompanySummaryDTO;
import com.highfive.meetu.domain.company.common.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Company(기업) 관련 JPA Repository
 *
 * 제공 기능:
 * - 회사 기본 조회
 * - 사업자등록번호, accountId 등으로 조회
 * - 상태별 필터링, 참여 통계 등
 */
@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    /**
     * 회사명으로 회사 조회
     */
    Optional<Company> findByName(String name);

    /**
     * 사업자등록번호로 회사 조회
     */
    Optional<Company> findByBusinessNumber(String businessNumber);

    /**
     * 상태(status) 기준으로 회사 목록 조회
     */
    List<Company> findAllByStatus(Integer status);

    /**
     * ✅ accountId 기준으로 회사 조회 (JOIN accountList)
     */
    @Query("SELECT c FROM company c JOIN c.accountList a WHERE a.id = :accountId")
    Optional<Company> findByAccountId(@Param("accountId") Long accountId);

    /**
     * ✅ 면접 후기가 등록된 기업 목록 요약 조회
     */
    @Query("""
        SELECT new com.highfive.meetu.domain.application.personal.dto.InterviewCompanySummaryDTO(
            c.id, c.name, COUNT(r), c.industry, c.logoKey, c.address, c.businessNumber, c.website
        )
        FROM interviewReview r
        JOIN r.company c
        WHERE r.status = 0
        GROUP BY c.id, c.name, c.industry, c.logoKey, c.address, c.businessNumber, c.website
        ORDER BY COUNT(r) DESC
    """)
    List<InterviewCompanySummaryDTO> findCompaniesWithReviews();

    /**
     * ✅ 최근 생성된 기업 수
     */
    @Query("""
        SELECT COUNT(c.id)
        FROM company c
        WHERE c.createdAt >= :start
    """)
    long countCompaniesCurrent(@Param("start") LocalDateTime start);

    /**
     * ✅ 이전 기간 내 생성된 기업 수
     */
    @Query("""
        SELECT COUNT(c.id)
        FROM company c
        WHERE c.createdAt BETWEEN :start AND :end
    """)
    long countCompaniesPrevious(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * ✅ 전체 참여 기업 수
     */
    @Query("""
        SELECT COUNT(c.id)
        FROM company c
    """)
    long countParticipatingCompanies();

    /**
     * 활성(ACTIVE) 상태인 회사만 조회
     *
     * @param id 회사 ID
     * @param status Company.Status.ACTIVE
     * @return Optional<Company>
     */
    Optional<Company> findByIdAndStatus(Long id, Integer status);
}
