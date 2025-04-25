package com.highfive.meetu.domain.company.common.repository;

import com.highfive.meetu.domain.company.common.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
/**
 * Company 엔티티 조회용 Repository
 */
public interface CompanyRepository extends JpaRepository<Company, Long> {
    /**
     * 회사명으로 검색
     * @param name 회사명
     * @return Optional<Company>
     */
    Optional<Company> findByName(String name);

    Optional<Company> findByBusinessNumber(String businessNumber);
}
