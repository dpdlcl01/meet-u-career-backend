package com.highfive.meetu.domain.company.personal.controller;

import com.highfive.meetu.domain.company.common.entity.Company;
import com.highfive.meetu.domain.company.personal.dto.*;
import com.highfive.meetu.domain.company.personal.service.CompanyBlockService;
import com.highfive.meetu.global.common.response.ResultData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * [개인회원] 차단 기업 관리 API 컨트롤러
 * - 기업 차단/해제
 * - 차단 목록 조회
 */
@RestController
@RequestMapping("/api/personal/companyblock")
@RequiredArgsConstructor
public class CompanyBlockController {

    private final CompanyBlockService companyBlockService;

    /**
     * 회사명으로 활성 상태 회사 검색 API
     *
     * @param request 검색어를 담은 요청 DTO
     * @return 검색 결과
     */
    @PostMapping("/search")
    public ResultData<List<CompanySearchResponseDTO>> searchActiveCompanies(@RequestBody CompanySearchRequestDTO request) {
        List<CompanySearchResponseDTO> result = companyBlockService.searchActiveCompaniesByName(request.getKeyword());
        return ResultData.success(result.size(), result);
    }

    /**
     * 차단한 기업 목록 조회
     *
     * @return 차단된 기업 리스트
     */
    @GetMapping("/list")
    public ResultData<List<CompanyBlockResponseDTO>> getBlockedCompanies() {
        List<CompanyBlockResponseDTO> blockedList = companyBlockService.getBlockedCompanies();
        return ResultData.success(blockedList.size(), blockedList);
    }

    /**
     * 차단된 기업 목록에서 기업명으로 검색
     *
     * @param request 검색어를 담은 요청 DTO
     * @return 검색된 차단된 기업 리스트
     */
    @PostMapping("/listsearch")
    public ResultData<List<CompanyBlockResponseDTO>> searchBlockedCompanies(@RequestBody CompanySearchRequestDTO request) {
        List<CompanyBlockResponseDTO> result = companyBlockService.searchBlockedCompaniesByName(request.getKeyword());
        return ResultData.success(result.size(), result);
    }

    /**
     * 기업 차단 요청
     *
     * @param request 차단할 기업 ID 리스트를 담은 요청 DTO
     * @return 처리 결과
     */
    @PostMapping("/block")
    public ResultData<?> blockCompany(@RequestBody CompanyBlockRequestDTO request) {
        List<Long> companyIds = request.getCompanyIds();

        // 기업 차단 로직 처리
        companyIds.forEach(companyId -> companyBlockService.blockCompany(companyId));
        return ResultData.success(companyIds.size(), "기업 차단이 완료되었습니다.");
    }

    /**
     * 기업 차단 해제 요청
     *
     * @param request 차단 해제할 기업 ID를 담은 요청 DTO
     * @return 처리 결과
     */
    @PostMapping("/unblock")
    public ResultData<?> unblockCompany(@RequestBody CompanyUnblockRequestDTO request) {
        companyBlockService.unblockCompany(request.getCompanyId());
        return ResultData.success(1, "기업 차단이 해제되었습니다.");
    }
}
