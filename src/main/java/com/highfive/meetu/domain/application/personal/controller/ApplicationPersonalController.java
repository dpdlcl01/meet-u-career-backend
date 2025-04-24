package com.highfive.meetu.domain.application.personal.controller;

import com.highfive.meetu.domain.application.personal.dto.ApplicationPersonalDTO;
import com.highfive.meetu.domain.application.personal.service.ApplicationPersonalService;
import com.highfive.meetu.global.common.response.ResultData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 개인 회원 마이페이지: 지원 내역 조회 컨트롤러
 */
@RestController
@RequestMapping("/api/personal/mypage/applications")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ApplicationPersonalController {

  private final ApplicationPersonalService applicationPersonalService;

  /**
   * 지원 내역 목록 조회 (마이페이지)
   * profileId는 우선 @RequestParam으로 받거나, security에서 추출할 수도 있음
   */
  @GetMapping
  public ResultData<List<ApplicationPersonalDTO>> getMyApplications(@RequestParam Long profileId) {
    // 예: securityUtil.getProfileId() 대신 파라미터로 임시 처리
    List<ApplicationPersonalDTO> list = applicationPersonalService.getMyApplications(profileId);
    return ResultData.success(list.size(), list);
  }

  /**
   * 특정 profileId와 status에 따른 지원 내역 목록 조회
   */
  @GetMapping("/search")
  public ResultData<List<ApplicationPersonalDTO>> searchApplications(

      @RequestParam Long profileId,
      @RequestParam(required = false) Integer status) {
    List<ApplicationPersonalDTO> list = applicationPersonalService.getApplicationsByProfileIdAndStatus(profileId,
        status);
    return ResultData.success(list.size(), list);
  }

  /**
   * 지원 취소 (마이페이지)
   */
  @PutMapping("/{applicationId}")
  public ResultData<?> cancelApplication(@PathVariable Long applicationId, @RequestParam Long profileId) {
    applicationPersonalService.cancelApplication(applicationId, profileId);
    return ResultData.success(1, "지원이 취소되었습니다.");
  }
}