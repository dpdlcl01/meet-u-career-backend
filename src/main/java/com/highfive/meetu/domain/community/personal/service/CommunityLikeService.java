package com.highfive.meetu.domain.community.personal.service;

import com.highfive.meetu.domain.community.common.entity.CommunityLike;
import com.highfive.meetu.domain.community.common.entity.CommunityPost;
import com.highfive.meetu.domain.community.common.repository.CommunityLikeRepository;
import com.highfive.meetu.domain.community.common.repository.CommunityPostRepository;
import com.highfive.meetu.domain.community.personal.dto.CommunityLikeDTO;
import com.highfive.meetu.domain.user.common.entity.Account;
import com.highfive.meetu.domain.user.common.repository.AccountRepository;
import com.highfive.meetu.global.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 커뮤니티 좋아요 서비스
 */
@Service
@RequiredArgsConstructor
public class CommunityLikeService {

  private final CommunityLikeRepository communityLikeRepository;
  private final CommunityPostRepository communityPostRepository;
  private final AccountRepository accountRepository;

  /**
   * 좋아요 토글 기능
   * - 이미 좋아요가 되어 있으면 해제
   * - 좋아요가 안 되어 있으면 등록
   *
   * @param accountId 사용자 ID
   * @param postId 게시글 ID
   * @return 등록된 경우 CommunityLikeDTO, 해제된 경우 null
   */
  @Transactional
  public CommunityLikeDTO toggleLike(Long accountId, Long postId) {

    // 🔸 사용자 존재 여부 확인
    Account account = accountRepository.findById(accountId)
        .orElseThrow(() -> new NotFoundException("사용자 정보를 찾을 수 없습니다."));

    // 🔸 게시글 존재 여부 확인
    CommunityPost post = communityPostRepository.findById(postId)
        .orElseThrow(() -> new NotFoundException("게시글 정보를 찾을 수 없습니다."));

    // 🔸 좋아요 존재 여부 확인
    CommunityLike existingLike = communityLikeRepository.findByAccountIdAndPostId(accountId, postId)
        .orElse(null);

    if (existingLike != null) {
      // 좋아요가 이미 존재하는 경우 → 삭제 처리
      communityLikeRepository.delete(existingLike);

      // 게시글의 좋아요 수 감소 (음수 방지)
      post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
      communityPostRepository.save(post);

      // 좋아요 해제 → null 반환
      return null;
    }

    // 🔸 좋아요가 없으면 → 새로 등록
    CommunityLike newLike = CommunityLike.builder()
        .account(account)
        .post(post)
        .build();
    communityLikeRepository.save(newLike);

    // 게시글 좋아요 수 증가
    post.setLikeCount(post.getLikeCount() + 1);
    communityPostRepository.save(post);

    // 🔸 DTO로 변환하여 반환
    return CommunityLikeDTO.builder()
        .id(newLike.getId())
        .accountId(accountId)
        .postId(postId)
        .createdAt(newLike.getCreatedAt())
        .build();
  }
}
