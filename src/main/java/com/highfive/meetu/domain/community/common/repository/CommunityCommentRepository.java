package com.highfive.meetu.domain.community.common.repository;

import com.highfive.meetu.domain.community.common.entity.CommunityComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {

    /**
     * 특정 게시글에 달린 댓글 목록 조회
     * - 상태가 ACTIVE(0)인 댓글만 조회
     * - 작성일 기준 오름차순 정렬 (최신 댓글이 가장 아래)
     */
    List<CommunityComment> findAllByPostIdAndStatusOrderByCreatedAtAsc(Long postId, Integer status);

    /**
     * 특정 사용자가 작성한 댓글 목록 조회
     * - 상태가 ACTIVE(0)인 댓글만 조회
     * - 작성일 기준 내림차순 정렬 (최신 댓글이 가장 위)
     */
    List<CommunityComment> findAllByAccountIdAndStatusOrderByCreatedAtDesc(Long accountId, Integer status);

    /**
     * 특정 게시글의 댓글 + 작성자 + 프로필까지 포함하여 조회
     * - 상태가 ACTIVE(0)인 댓글만 조회
     * - 작성일 기준 오름차순 정렬
     * - 댓글 → 작성자 Account → Profile까지 Fetch Join
     */
    @Query("""
        SELECT c FROM communityComment c
        JOIN FETCH c.account a
        LEFT JOIN FETCH a.profile p
        WHERE c.post.id = :postId AND c.status = 0
        ORDER BY c.createdAt ASC
    """)
    List<CommunityComment> findAllByPostIdWithAccountAndProfile(Long postId);

    /**
     * 상태값에 따른 전체 댓글 목록 조회
     * - 관리자용 API 또는 통계용 조회
     */
    Page<CommunityComment> findAllByStatus(Integer status, Pageable pageable);
}
