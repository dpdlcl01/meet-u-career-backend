// src/main/java/com/highfive/meetu/domain/company/admin/controller/CompanyFinancialController.java
package com.highfive.meetu.domain.company.admin.controller;

import com.highfive.meetu.domain.company.admin.service.Company2FinancialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

/**
 * 관리자 전용 컨트롤러
 * • /admin/update-corp-basic-info : 금융위원회_기업기본정보 API 기반 업데이트
 */
@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class Company2FinancialController {

  private final Company2FinancialService financialService;

  /**
   * 기업 기본정보(사업자등록번호, 직원수, 평균급여) 업데이트 엔드포인트
   *
   * @return { "updated": 갱신건수, "skipped": 스킵건수 }
   */
  @GetMapping("/update-corp-basic-info") // allow GET as user expects
  public ResponseEntity<Map<String, Integer>> updateCorpBasicInfo() {
    Map<String, Integer> result = financialService.updateCorpBasicInfo();
    return ResponseEntity.ok(result);
  }
}