// src/main/java/com/highfive/meetu/domain/system/admin/controller/SystemLogAdminController.java
package com.highfive.meetu.domain.system.admin.controller;

import com.highfive.meetu.domain.system.admin.dto.SystemLogAdminDTO;
import com.highfive.meetu.domain.system.admin.service.SystemLogAdminService;
import com.highfive.meetu.global.common.response.ResultData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 관리자용 시스템 로그 조회 컨트롤러
 */
@Tag(name = "system-log-admin-controller", description = "관리자 시스템 로그 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/logs")
public class SystemLogAdminController {

  private final SystemLogAdminService systemLogAdminService;

  /**
   * 모든 활성 시스템 로그를 생성일시 내림차순으로 조회하여 반환
   *
   * @return ResultData 에 담긴 SystemLogAdminDTO 리스트
   */
  @Operation(summary = "시스템 로그 전체 조회")
  @GetMapping
  public ResultData<List<SystemLogAdminDTO>> getAllLogs() {
    List<SystemLogAdminDTO> logs = systemLogAdminService.getLogList();
    return ResultData.success(logs.size(), logs);
  }
}