package com.highfive.meetu.domain.community.common.repository;

import com.highfive.meetu.domain.community.common.entity.CommunityTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityTagRepository extends JpaRepository<CommunityTag, Long> {

  // 사용 중인 태그만 조회
  List<CommunityTag> findAllByStatus(Integer status);
}
