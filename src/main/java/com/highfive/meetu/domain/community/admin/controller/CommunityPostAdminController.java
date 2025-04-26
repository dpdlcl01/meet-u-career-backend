package com.highfive.meetu.domain.community.admin.controller;

import com.highfive.meetu.domain.community.admin.dto.CommunityPostDTO;
import com.highfive.meetu.domain.community.admin.dto.CommunityPostSearchRequestDTO;
import com.highfive.meetu.domain.community.admin.dto.PostDeleteRequestDTO;
import com.highfive.meetu.domain.community.admin.service.CommunityPostAdminService;
import com.highfive.meetu.global.common.response.ResultData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 커뮤니티 게시글 관리 API (관리자 전용)
 */
@RestController
@RequestMapping("/api/admin/community/posts")
@RequiredArgsConstructor
public class CommunityPostAdminController {

    private final CommunityPostAdminService communityPostAdminService;

    /**
     * 게시글 검색
     *
     * @param request 검색 조건 (status, page, size, sortBy, direction)
     */
    @PostMapping("/search")
    public ResultData<Page<CommunityPostDTO>> searchPosts(@RequestBody CommunityPostSearchRequestDTO request) {
        Page<CommunityPostDTO> posts = communityPostAdminService.searchPosts(request);
        return ResultData.success(posts.getSize(), posts);
    }

    /**
     * 게시글 전체 목록 조회 (페이징 없음)
     */
    @GetMapping("/all")
    public ResultData<List<CommunityPostDTO>> getAllPosts() {
        List<CommunityPostDTO> posts = communityPostAdminService.findAllPosts();
        return ResultData.success(posts.size(), posts);
    }

    /**
     * 게시글 상세 조회
     */
    @GetMapping("/{postId}")
    public ResultData<CommunityPostDTO> getPostDetail(@PathVariable Long postId) {
        CommunityPostDTO post = communityPostAdminService.getPostDetail(postId);
        return ResultData.success(1, post);
    }

    /**
     * 게시글 상태 토글 (활성/비활성)
     */
    @PatchMapping("/{postId}/status")
    public ResultData<Void> togglePostStatus(@PathVariable Long postId) {
        communityPostAdminService.togglePostStatus(postId);
        return ResultData.success(1, null);
    }

    /**
     * 게시글 삭제 (상태만 변경)
     * @param request 게시글 삭제 요청 DTO
     * @return 성공 메시지
     */
    @PostMapping("/delete")
    public ResultData<String> deletePost(@RequestBody PostDeleteRequestDTO request) {
        communityPostAdminService.deletePost(request.getPostId());
        return ResultData.success(1, null);
    }
}
