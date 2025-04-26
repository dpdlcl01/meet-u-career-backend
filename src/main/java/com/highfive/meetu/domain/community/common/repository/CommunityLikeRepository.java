package com.highfive.meetu.domain.community.common.repository;

import com.highfive.meetu.domain.community.common.entity.CommunityLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommunityLikeRepository extends JpaRepository<CommunityLike, Long> {

  // 사용자 + 게시글로 좋아요 여부 조회
  Optional<CommunityLike> findByAccountIdAndPostId(Long accountId, Long postId);
    
}
