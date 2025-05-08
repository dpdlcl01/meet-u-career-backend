// src/main/java/com/highfive/meetu/domain/system/common/repository/SystemLogRepository.java
package com.highfive.meetu.domain.system.common.repository;

import com.highfive.meetu.domain.system.common.entity.SystemLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 시스템 로그 JPA 리포지토리
 */
@Repository
public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {

  /**
   * 모든 시스템 로그를 생성일시 기준 내림차순으로 조회
   *
   * @return 생성일 내림차순 정렬된 SystemLog 리스트
   */
  List<SystemLog> findAllByOrderByCreatedAtDesc();
}