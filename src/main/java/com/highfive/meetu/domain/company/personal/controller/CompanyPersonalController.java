// // src/main/java/com/highfive/meetu/domain/company/personal/controller/CompanyPersonalController.java
// package com.highfive.meetu.domain.company.personal.controller;
//
// import com.highfive.meetu.domain.company.personal.dto.CompanyPersonalDTO;
// import com.highfive.meetu.domain.company.personal.service.CompanyPersonalService;
// import com.highfive.meetu.global.common.response.ResultData;
// import lombok.RequiredArgsConstructor;
// import org.springframework.web.bind.annotation.*;
//
// @RestController
// @RequestMapping("/api/personal/companies")
// @RequiredArgsConstructor
// public class CompanyPersonalController {
//
//   private final CompanyPersonalService companyPersonalService;
//
//   /**
//    * 회사 정보 조회 API
//    *
//    * Sidebar 및 Company Introduction 에 필요한 필드를 함께 반환
//    *
//    * GET /api/personal/companies/{companyId}/info
//    */
//   @GetMapping("/{companyId}/info")
//   public ResultData<CompanyPersonalDTO> getCompanyInfo(@PathVariable Long companyId) {
//     CompanyPersonalDTO dto = companyPersonalService.getCompanyInfo(companyId);
//     return ResultData.success(1, dto);
//   }
// }

// src/main/java/com/highfive/meetu/domain/company/personal/controller/CompanyPersonalController.java
package com.highfive.meetu.domain.company.personal.controller;

import com.highfive.meetu.domain.company.personal.dto.CompanyInfoDTO;
import com.highfive.meetu.domain.company.personal.service.CompanyPersonalService;
import com.highfive.meetu.global.common.response.ResultData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 개인(구직자)용 Company API 컨트롤러
 */
@RestController
@RequestMapping("/api/personal/companies")
@RequiredArgsConstructor
public class CompanyPersonalController {

  private final CompanyPersonalService companyPersonalService;

  /**
   * 회사 상세 정보 조회
   *
   * GET /api/personal/companies/{companyId}/info
   *
   * @param companyId PathVariable로 받은 회사 ID
   * @return ResultData<CompanyInfoDTO>
   */
  @GetMapping("/{companyId}/info")
  public ResultData<CompanyInfoDTO> getCompanyInfo(@PathVariable Long companyId) {
    CompanyInfoDTO dto = companyPersonalService.getCompanyInfo(companyId);
    return ResultData.success(1, dto);
  }
}
