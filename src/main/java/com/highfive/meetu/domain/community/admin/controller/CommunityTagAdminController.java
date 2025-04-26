package com.highfive.meetu.domain.community.admin.controller;

import com.highfive.meetu.domain.community.admin.dto.CommunityTagDTO;
import com.highfive.meetu.domain.community.admin.dto.CreateCommunityTagRequest;
import com.highfive.meetu.domain.community.admin.dto.TagSearchRequestDTO;
import com.highfive.meetu.domain.community.admin.dto.UpdateTagRequestDTO;
import com.highfive.meetu.domain.community.admin.service.CommunityTagAdminService;
import com.highfive.meetu.global.common.response.ResultData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 커뮤니티 태그 관리 API (관리자 전용)
 */
@RestController
@RequestMapping("/api/admin/community/tags")
@RequiredArgsConstructor
public class CommunityTagAdminController {

    private final CommunityTagAdminService communityTagAdminService;

    /**
     * 태그 생성
     * @param request 태그 생성 요청 DTO
     * @return 생성된 태그 ID
     */
    @PostMapping
    public ResultData<Long> createTag(@RequestBody CreateCommunityTagRequest request) {
        Long tagId = communityTagAdminService.createTag(request.getName(), request.getStatus());
        return ResultData.success(1, tagId);
    }

    /**
     * 태그 페이징 검색
     * @param request 검색 조건 (status, page, size, sortBy, direction)
     * @return 페이징된 태그 리스트
     */
    @PostMapping("/search")
    public ResultData<Page<CommunityTagDTO>> searchTags(@RequestBody TagSearchRequestDTO request) {
        Page<CommunityTagDTO> tagPage = communityTagAdminService.getTags(request);
        return ResultData.success(tagPage.getSize(), tagPage);
    }

    /**
     * 전체 태그 목록 조회
     * (페이징 없이 모든 태그 반환)
     * @return 태그 리스트
     */
    @GetMapping("/all")
    public ResultData<List<CommunityTagDTO>> getAllTags() {
        List<CommunityTagDTO> tagList = communityTagAdminService.findAllTags();
        return ResultData.success(tagList.size(), tagList);
    }

    /**
     * 태그 상태 토글 (활성 ↔ 비활성)
     * @param tagId 태그 ID
     * @return 성공 여부
     */
    @PatchMapping("/{tagId}/status")
    public ResultData<Void> toggleStatus(@PathVariable Long tagId) {
        communityTagAdminService.toggleStatus(tagId);
        return ResultData.success(1, null);
    }

    /**
     * 태그 수정 API
     * @param dto 수정할 태그 정보 (id, name, status)
     * @return 수정된 태그 ID
     */
    @PutMapping
    public ResultData<Long> updateTag(@RequestBody UpdateTagRequestDTO dto) {
        Long updatedId = communityTagAdminService.updateTag(dto.getId(), dto.getName(), dto.getStatus());
        return ResultData.success(1, updatedId);
    }
}
