package com.highfive.meetu.domain.community.common.repository;

import com.highfive.meetu.domain.community.common.entity.CommunityPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {

    // 전체 게시글 조회 (최신순 정렬 / 중앙영역)
    List<CommunityPost> findAllByStatusOrderByCreatedAtDesc(Integer status);

    // 내가 쓴 글 조회 (최신순 정렬)
    List<CommunityPost> findAllByAccountIdAndStatusOrderByCreatedAtDesc(Long accountId, Integer status);

    // 해시태그별 게시글 조회 (최신순 정렬 / 중앙영역)
    List<CommunityPost> findAllByTagIdAndStatusOrderByCreatedAtDesc(Long tagId, Integer status);

    // 상태별 게시글 조회
    Page<CommunityPost> findAllByStatus(Integer status, Pageable pageable);
}
