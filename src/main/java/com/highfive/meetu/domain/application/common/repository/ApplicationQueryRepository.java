package com.highfive.meetu.domain.application.common.repository;

import com.highfive.meetu.domain.application.personal.dto.ApplicationPersonalDTO;

import java.util.List;

/**
 * Application에 대한 복잡한 조회를 담당하는 QueryDSL Repository 인터페이스
 */
public interface ApplicationQueryRepository {

    /**
     * 특정 profileId(개인회원)의 지원 내역 목록 조회
     * - Application + JobPosting + Company 등 조인하여 DTO로 반환
     */
    List<ApplicationPersonalDTO> findAllByProfileId(Long profileId);
}