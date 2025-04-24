package com.highfive.meetu.domain.community.personal.controller;

import com.highfive.meetu.domain.community.personal.dto.CommunityLikeDTO;
import com.highfive.meetu.domain.community.personal.service.CommunityLikeService;
import com.highfive.meetu.global.common.response.ResultData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 커뮤니티 좋아요 컨트롤러
 */
@RestController
@RequestMapping("/api/community/likes")
@RequiredArgsConstructor
public class CommunityLikeController {

  private final CommunityLikeService communityLikeService;

  /**
   * 좋아요 토글 (좋아요 추가 또는 해제)
   *
   * @param dto accountId, postId를 담은 DTO
   * @return 좋아요 추가 시 CommunityLikeDTO, 해제 시 null
   */
  @PostMapping("/toggle")
  public ResultData<CommunityLikeDTO> toggleLike(@RequestBody CommunityLikeDTO dto) {
    CommunityLikeDTO result = communityLikeService.toggleLike(dto.getAccountId(), dto.getPostId());

    if (result == null) {
      // 좋아요 해제 시
      return ResultData.success(0, null);
    } else {
      // 좋아요 등록 시
      return ResultData.success(1, result);
    }
  }
}
