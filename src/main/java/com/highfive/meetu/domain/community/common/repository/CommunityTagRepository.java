package com.highfive.meetu.domain.community.common.repository;

import com.highfive.meetu.domain.community.common.entity.CommunityTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 커뮤니티 태그 레포지토리
 */
@Repository
public interface CommunityTagRepository extends JpaRepository<CommunityTag, Long> {

    /**
     * 특정 상태의 모든 태그 조회
     * - 예: status = 0 → 활성 태그만
     */
    List<CommunityTag> findAllByStatus(Integer status);

    /**
     * 상태값으로 태그 조회 (기타 필터용)
     */
    Page<CommunityTag> findAllByStatus(Integer status, Pageable pageable);
}
