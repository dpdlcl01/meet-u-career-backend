// src/main/java/com/highfive/meetu/domain/system/admin/service/SystemLogAdminService.java
package com.highfive.meetu.domain.system.admin.service;

import com.highfive.meetu.domain.system.admin.dto.SystemLogAdminDTO;
import com.highfive.meetu.domain.system.common.entity.SystemLog;
import com.highfive.meetu.domain.system.common.repository.SystemLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 관리자용 시스템 로그 조회 서비스
 */
@Service
@RequiredArgsConstructor
public class SystemLogAdminService {

  private final SystemLogRepository systemLogRepository;

  /**
   * 전체 시스템 로그를 생성일시 기준 내림차순으로 조회하고 DTO로 변환하여 반환
   */
  public List<SystemLogAdminDTO> getLogList() {
    List<SystemLog> logs = systemLogRepository.findAllByOrderByCreatedAtDesc();

    return logs.stream()
        .map(log -> {
          // 1) userId 결정
          String userId = log.getAccount() != null
              ? log.getAccount().getUserId()
              : log.getAdmin()   != null
              ? log.getAdmin().getEmail()
              : null;

          // 2) logType 정수를 문자열로 매핑
          String typeName;
          switch (log.getLogType()) {
            case SystemLog.LogType.SECURITY:    typeName = "SECURITY";    break;
            case SystemLog.LogType.TRANSACTION: typeName = "TRANSACTION"; break;
            case SystemLog.LogType.USER:        typeName = "USER";        break;
            case SystemLog.LogType.ADMIN:       typeName = "SYSTEM";      break; // ADMIN 로그는 프론트에서 “SYSTEM”으로 보여줌
            case SystemLog.LogType.ERROR:       typeName = "ERROR";       break;
            default:                            typeName = "UNKNOWN";     break;
          }

          return SystemLogAdminDTO.builder()
              .id(log.getId())
              .userId(userId)
              .type(typeName)
              .module(log.getModule())
              .action(log.getAction())
              .ipAddress(log.getIpAddress())
              .createdAt(log.getCreatedAt())
              .build();
        })
        .collect(Collectors.toList());
  }
}