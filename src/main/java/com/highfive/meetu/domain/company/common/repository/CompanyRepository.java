package com.highfive.meetu.domain.company.common.repository;

import com.highfive.meetu.domain.company.common.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Company(기업) 관련 JPA Repository
 *
 * 제공 기능:
 * - 회사 이름(name)으로 회사 조회
 * - 사업자등록번호(businessNumber)로 회사 조회
 * - 상태(status)별 회사 목록 조회 (ex. 활성화된 회사만)
 */
@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    /**
     * 회사명으로 회사 조회
     *
     * @param name 회사명
     * @return Optional<Company> (존재하지 않을 수도 있음)
     */
    Optional<Company> findByName(String name);

    /**
     * 사업자등록번호로 회사 조회
     *
     * @param businessNumber 사업자등록번호
     * @return Optional<Company> (존재하지 않을 수도 있음)
     */
    Optional<Company> findByBusinessNumber(String businessNumber);

    /**
     * 상태(status) 기준으로 회사 목록 조회
     * (예: status = 0 이면 활성화된 회사만 조회)
     *
     * @param status 회사 상태 코드
     * @return List<Company> 해당 상태의 회사 리스트
     */
    List<Company> findAllByStatus(Integer status);
}
