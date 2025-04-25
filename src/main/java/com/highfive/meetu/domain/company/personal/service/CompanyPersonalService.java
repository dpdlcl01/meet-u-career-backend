// src/main/java/com/highfive/meetu/domain/company/personal/service/CompanyPersonalService.java
package com.highfive.meetu.domain.company.personal.service;

import com.highfive.meetu.domain.company.common.entity.Company;
import com.highfive.meetu.domain.company.common.repository.CompanyRepository;
import com.highfive.meetu.domain.company.personal.dto.CompanyPersonalDTO;
import com.highfive.meetu.global.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyPersonalService {

  private final CompanyRepository companyRepository;

  /**
   * 회사 정보 조회
   *
   * @param companyId 회사 고유 ID
   * @return CompanyPersonalDTO (Sidebar 및 Introduction 용)
   * @throws NotFoundException 회사가 존재하지 않을 경우
   */
  public CompanyPersonalDTO getCompanyInfo(Long companyId) {
    Company company = companyRepository.findById(companyId)
        .orElseThrow(() -> new NotFoundException("존재하지 않는 회사입니다. id=" + companyId));

    // DTO 빌드 및 반환
    return CompanyPersonalDTO.builder()
        .logoKey(company.getLogoKey())           // 로고 S3 키
        .name(company.getName())                 // 회사명
        .companyType(company.getCompanyType())   // 기업 형태
        .industry(company.getIndustry())         // 업종
        .location(company.getAddress())          // 주소 → Sidebar의 location
        .foundedDate(company.getFoundedDate())   // 설립일
        .numEmployees(company.getNumEmployees()) // 직원 수
        .build();
  }
}