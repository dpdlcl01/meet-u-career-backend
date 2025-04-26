package com.highfive.meetu.domain.community.admin.controller;

import com.highfive.meetu.domain.community.admin.dto.CommentDeleteRequestDTO;
import com.highfive.meetu.domain.community.admin.dto.CommentSearchRequestDTO;
import com.highfive.meetu.domain.community.admin.dto.CommunityCommentDTO;
import com.highfive.meetu.domain.community.admin.service.CommunityCommentAdminService;
import com.highfive.meetu.global.common.response.ResultData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 커뮤니티 댓글 관리 컨트롤러 (관리자용)
 */
@RestController
@RequestMapping("/api/admin/community/comments")
@RequiredArgsConstructor
public class CommunityCommentAdminController {

    private final CommunityCommentAdminService communityCommentAdminService;

    /**
     * 전체 댓글 목록 조회 (status로 필터링 가능)
     */
    @PostMapping("/search")
    public ResultData<Page<CommunityCommentDTO>> searchComments(@RequestBody CommentSearchRequestDTO request) {
        Page<CommunityCommentDTO> comments = communityCommentAdminService.getAllComments(request);
        return ResultData.success(comments.getSize(), comments);
    }

    /**
     * 전체 댓글 목록 조회 (페이징 없이)
     */
    @GetMapping("/all")
    public ResultData<List<CommunityCommentDTO>> getAllComments() {
        List<CommunityCommentDTO> comments = communityCommentAdminService.findAllComments();
        return ResultData.success(comments.size(), comments);
    }

    /**
     * 단일 댓글 상세 조회
     */
    @GetMapping("/{commentId}")
    public ResultData<CommunityCommentDTO> getComment(@PathVariable Long commentId) {
        return ResultData.success(1, communityCommentAdminService.getCommentDetail(commentId));
    }

    /**
     * 댓글 상태 토글 (활성 <-> 비활성)
     */
    @PatchMapping("/{commentId}/status")
    public ResultData<Void> toggleStatus(@PathVariable Long commentId) {
        communityCommentAdminService.toggleStatus(commentId);
        return ResultData.success(1, null);
    }

    /**
     * 댓글 삭제 (상태만 변경)
     * @param request 댓글 삭제 요청 DTO
     * @return 성공 메시지
     */
    @PostMapping("/delete")
    public ResultData<String> deleteComment(@RequestBody CommentDeleteRequestDTO request) {
        communityCommentAdminService.deleteComment(request.getCommentId());
        return ResultData.success(1, null);
    }
}
