package com.highfive.meetu.domain.community.common.repository;

import com.highfive.meetu.domain.community.common.entity.CommunityComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {

  // 특정 게시글의 댓글 리스트 (정상 상태만)
  List<CommunityComment> findAllByPostIdAndStatusOrderByCreatedAtAsc(Long postId, Integer status);

  List<CommunityComment> findAllByAccountIdAndStatusOrderByCreatedAtDesc(Long accountId, Integer status);

  @Query("SELECT c FROM communityComment c JOIN FETCH c.account a LEFT JOIN FETCH a.profile p WHERE c.post.id = :postId AND c.status = 0 ORDER BY c.createdAt ASC")
  List<CommunityComment> findAllByPostIdWithAccountAndProfile(Long postId);


}


