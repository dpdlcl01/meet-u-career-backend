// src/main/java/com/highfive/meetu/domain/company/personal/controller/CompanyFinancialController.java
package com.highfive.meetu.domain.company.personal.controller;

import com.highfive.meetu.domain.company.personal.service.CompanyFinancialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 관리자 전용 재무정보 업데이트 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class CompanyFinancialController {

  private final CompanyFinancialService financialService;

  /**
   * DART API를 통해 기업 재무정보(매출액, 당기순이익)를 갱신
   *
   * @param year       조회 연도
   * @param reprtCode  보고서 코드
   * @return 업데이트 및 스킵 카운트
   */
  @PostMapping("/update-financials")
  public ResponseEntity<Map<String, Integer>> update(
      @RequestParam int year,
      @RequestParam String reprtCode
  ) {
    Map<String, Integer> result = financialService.updateFinancials(year, reprtCode);
    return ResponseEntity.ok(result);
  }
}