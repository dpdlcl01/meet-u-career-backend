// src/main/java/com/highfive/meetu/domain/company/admin/controller/CompanyFinancialController.java
package com.highfive.meetu.domain.company.admin.controller;

import com.highfive.meetu.domain.company.admin.service.CompanyFinancialService;
import com.highfive.meetu.global.common.response.ResultData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 관리자 전용 컨트롤러
 * - 재무정보 업데이트 (/admin/update-financials)
 * - 직원통계 업데이트 (/admin/update-employee-stats)
 */
@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class CompanyFinancialController {

  private final CompanyFinancialService financialService;

  /**
   * 매출액·당기순이익 업데이트 API
   *
   * @param year      조회 연도
   * @param reprtCode 보고서 코드
   * @return ResultData에 업데이트/스킵 카운트 리턴
   */
  @PostMapping("/update-financials")
  public ResultData<Map<String, Integer>> updateFinancials(
      @RequestParam int year,
      @RequestParam String reprtCode) {
    Map<String, Integer> result = financialService.updateFinancials(year, reprtCode);
    int total = result.getOrDefault("updated", 0) + result.getOrDefault("skipped", 0);
    return ResultData.success(total, result);
  }

  /**
   * 직원 수·평균 급여 업데이트 API
   *
   * @param year      조회 연도
   * @param reprtCode 보고서 코드
   * @return ResultData에 업데이트/스킵 카운트 리턴
   */
  @PostMapping("/update-employee-stats")
  public ResultData<Map<String, Integer>> updateEmployeeStats(
      @RequestParam int year,
      @RequestParam String reprtCode) {
    Map<String, Integer> result = financialService.updateEmployeeStats(year, reprtCode);
    int total = result.getOrDefault("updated", 0) + result.getOrDefault("skipped", 0);
    return ResultData.success(total, result);
  }
}