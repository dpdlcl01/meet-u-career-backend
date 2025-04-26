package com.highfive.meetu.domain.community.personal.controller;

import com.highfive.meetu.domain.community.personal.dto.CommunityCommentDTO;
import com.highfive.meetu.domain.community.personal.service.CommunityCommentService;
import com.highfive.meetu.global.common.response.ResultData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/community/comments")
@RequiredArgsConstructor
public class CommunityCommentController {

  private final CommunityCommentService commentService;
  private final CommunityCommentService communityCommentService;

  // 댓글등록
  @PostMapping("/create")
  public ResultData<CommunityCommentDTO> create(@RequestBody CommunityCommentDTO dto) {
    CommunityCommentDTO result = commentService.createComment(dto);
    return ResultData.success(1, result);
  }

  // 댓글 목록조회
  @GetMapping("/{postId}")
  public ResultData<List<CommunityCommentDTO>> getList(@PathVariable Long postId) {
    List<CommunityCommentDTO> result = commentService.getCommentsByPost(postId);
    return ResultData.success(result.size(), result);
  }


  // 내가 쓴 게시글, 댓글 조회
  @GetMapping("/my/{accountId}")
  public ResultData<List<CommunityCommentDTO>> getMyComments(@PathVariable Long accountId) {
    List<CommunityCommentDTO> result = communityCommentService.getMyComments(accountId);
    return ResultData.success(result.size(), result);
  }


  //댓글 수정
  @PostMapping("/update")
  public ResultData<CommunityCommentDTO> updateComment(@RequestBody CommunityCommentDTO dto) {
    CommunityCommentDTO result = communityCommentService.updateComment(dto);
    return ResultData.success(1, result);
  }



  // 댓글 삭제
  @PostMapping("/delete/{commentId}")
  public ResultData<String> deleteComment(@PathVariable Long commentId) {
    communityCommentService.deleteComment(commentId);
    return ResultData.success(1, "댓글이 삭제되었습니다.");
  }

}
