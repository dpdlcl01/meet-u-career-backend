package com.highfive.meetu.domain.company.personal.service;

import com.highfive.meetu.domain.company.common.entity.Company;
import com.highfive.meetu.domain.company.common.entity.CompanyBlock;
import com.highfive.meetu.domain.company.common.repository.CompanyBlockRepository;
import com.highfive.meetu.domain.company.common.repository.CompanyRepository;
import com.highfive.meetu.domain.company.personal.dto.CompanyBlockResponseDTO;
import com.highfive.meetu.domain.company.personal.dto.CompanySearchResponseDTO;
import com.highfive.meetu.domain.user.common.entity.Profile;
import com.highfive.meetu.domain.user.common.repository.ProfileRepository;
import com.highfive.meetu.global.common.exception.NotFoundException;
import com.highfive.meetu.infra.oauth.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 개인회원 - 기업 차단 관리 서비스
 */
@Service
@RequiredArgsConstructor
public class CompanyBlockService {

    private final CompanyRepository companyRepository;
    private final ProfileRepository profileRepository;
    private final CompanyBlockRepository companyBlockRepository;

    /**
     * 활성화된 회사 리스트 조회
     */
    public List<Company> getActiveCompanies() {
        return companyRepository.findAllByStatus(Company.Status.ACTIVE);
    }

    /**
     * 회사명으로 활성 상태 회사 검색
     */
    public List<CompanySearchResponseDTO> searchActiveCompaniesByName(String keyword) {
        Long profileId = SecurityUtil.getProfileId();

        // 차단된 기업 목록 조회
        List<CompanyBlock> blockedCompanies = companyBlockRepository.findAllByProfileId(profileId);

        // 활성화된 회사명으로 검색
        List<Company> companies = companyRepository.findAllByStatus(Company.Status.ACTIVE)
            .stream()
            .filter(company -> company.getName().toLowerCase().contains(keyword.toLowerCase()))  // 회사명으로 필터링
            .filter(company -> !blockedCompanies.stream().anyMatch(block -> block.getCompany().equals(company)))  // 차단된 기업 제외
            .collect(Collectors.toList());

        return companies.stream()
            .map(company -> new CompanySearchResponseDTO(
                company.getId(),
                company.getName(),
                company.getBusinessNumber(),
                company.getRepresentativeName(),
                company.getIndustry(),
                company.getFoundedDate().toString(),
                company.getNumEmployees(),
                company.getRevenue(),
                company.getWebsite(),
                company.getLogoKey(),
                company.getAddress(),
                company.getCreatedAt()
            ))
            .collect(Collectors.toList());
    }

    /**
     * 로그인한 사용자가 차단한 기업 목록 조회
     *
     * @return 차단한 기업 리스트 (CompanyBlockResponseDTO)
     */
    @Transactional(readOnly = true)
    public List<CompanyBlockResponseDTO> getBlockedCompanies() {
        Long profileId = SecurityUtil.getProfileId();

        List<CompanyBlock> blocks = companyBlockRepository.findAllByProfileId(profileId);

        return blocks.stream()
            .map(CompanyBlockResponseDTO::fromEntity)
            .toList();
    }

    /**
     * 차단된 기업 목록에서 기업명으로 검색
     *
     * @param keyword 검색어
     * @return 검색된 차단된 기업 리스트
     */
    public List<CompanyBlockResponseDTO> searchBlockedCompaniesByName(String keyword) {
        Long profileId = SecurityUtil.getProfileId();  // 로그인한 사용자 프로필 ID

        // 차단된 기업 목록 조회
        List<CompanyBlock> blockedCompanies = companyBlockRepository.findAllByProfileId(profileId);

        // 검색어에 맞는 기업명을 포함한 기업 필터링
        List<Company> companies = blockedCompanies.stream()
            .map(CompanyBlock::getCompany)  // CompanyBlock에서 Company를 가져옵니다.
            .filter(company -> company.getName().toLowerCase().contains(keyword.toLowerCase()))  // 회사명으로 필터링
            .collect(Collectors.toList());

        return companies.stream()
            .map(company -> new CompanyBlockResponseDTO(
                company.getId(),
                company.getName(),
                company.getRepresentativeName(),
                company.getIndustry(),
                company.getLogoKey(),
                company.getAddress(),
                company.getCreatedAt()
            ))
            .collect(Collectors.toList());
    }

    /**
     * 기업 차단 설정
     *
     * @param companyId 차단할 기업 ID
     */
    @Transactional
    public void blockCompany(Long companyId) {
        Long profileId = SecurityUtil.getProfileId();

        // 프로필과 기업 정보 조회
        Profile profile = profileRepository.findById(profileId)
            .orElseThrow(() -> new NotFoundException("프로필을 찾을 수 없습니다."));
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new NotFoundException("기업을 찾을 수 없습니다."));

        // 이미 차단한 경우 예외 발생
        if (companyBlockRepository.existsByProfileAndCompany(profile, company)) {
            throw new IllegalStateException("이미 차단한 기업입니다.");
        }

        // 차단 등록
        CompanyBlock block = CompanyBlock.builder()
            .profile(profile)
            .company(company)
            .build();

        companyBlockRepository.save(block);
    }

    /**
     * 기업 차단 해제
     *
     * @param companyId 차단 해제할 기업 ID
     */
    @Transactional
    public void unblockCompany(Long companyId) {
        Long profileId = SecurityUtil.getProfileId();

        // 프로필과 기업 정보 조회
        Profile profile = profileRepository.findById(profileId)
            .orElseThrow(() -> new NotFoundException("프로필을 찾을 수 없습니다."));
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new NotFoundException("기업을 찾을 수 없습니다."));

        // 차단 기록 조회
        List<CompanyBlock> blocks = companyBlockRepository.findAllByProfileId(profileId);
        if (blocks.isEmpty()) {
            throw new NotFoundException("차단 기록이 없습니다.");
        }

        // 차단된 기업이 목록에 있는지 확인
        CompanyBlock block = blocks.stream()
            .filter(b -> b.getCompany().getId().equals(companyId))
            .findFirst()
            .orElseThrow(() -> new NotFoundException("차단된 기업이 아닙니다."));

        // 차단 해제
        companyBlockRepository.delete(block);
    }
}
