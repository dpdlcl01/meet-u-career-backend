package com.highfive.meetu.domain.community.admin.service;

import com.highfive.meetu.domain.community.admin.dto.CommunityTagDTO;
import com.highfive.meetu.domain.community.admin.dto.TagSearchRequestDTO;
import com.highfive.meetu.domain.community.common.entity.CommunityTag;
import com.highfive.meetu.domain.community.common.repository.CommunityTagRepository;
import com.highfive.meetu.global.common.exception.NotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 관리자용 커뮤니티 태그 서비스
 * 태그 등록, 수정, 상태 토글, 페이징 검색 기능 포함
 */
@Service
@RequiredArgsConstructor
public class CommunityTagAdminService {

    private final CommunityTagRepository communityTagRepository;

    /**
     * 태그 생성
     * @param name 태그명
     * @param status 상태값 (0: 활성, 1: 비활성)
     * @return 생성된 태그의 ID
     */
    public Long createTag(String name, int status) {
        CommunityTag tag = CommunityTag.builder()
            .name(name)
            .status(status)
            .build();
        communityTagRepository.save(tag);
        return tag.getId();
    }

    /**
     * 태그 목록 조회 (전체 또는 상태 기반 필터링)
     * @param request 검색 조건: 상태, 정렬, 페이지 정보
     * @return 검색 결과 Page<CommunityTagDTO>
     */
    public Page<CommunityTagDTO> getTags(TagSearchRequestDTO request) {
        Sort.Direction direction = Sort.Direction.fromString(request.getDirection());
        Pageable pageable = PageRequest.of(
            request.getPage(),
            request.getSize(),
            Sort.by(direction, request.getSortBy())
        );

        Page<CommunityTag> page = (request.getStatus() == null)
            ? communityTagRepository.findAll(pageable)
            : communityTagRepository.findAllByStatus(request.getStatus(), pageable);

        return page.map(CommunityTagDTO::from); // Entity → DTO 변환
    }

    /**
     * 전체 태그 목록 조회
     */
    @Transactional(readOnly = true)
    public List<CommunityTagDTO> findAllTags() {
        List<CommunityTag> tagList = communityTagRepository.findAll();

        return tagList.stream()
            .map(CommunityTagDTO::from)
            .toList();
    }

    /**
     * 태그 수정
     * @param id 수정할 태그의 ID
     * @param name 수정할 태그명
     * @param status 수정할 상태값
     * @return 수정된 태그 ID
     */
    @Transactional
    public Long updateTag(Long id, String name, int status) {
        CommunityTag tag = communityTagRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("해당 태그를 찾을 수 없습니다."));

        tag.setName(name);     // 이름 수정
        tag.setStatus(status); // 상태 수정

        return tag.getId();
    }

    /**
     * 태그 상태 토글 (활성 ↔ 비활성)
     * @param tagId 대상 태그 ID
     */
    @Transactional
    public void toggleStatus(Long tagId) {
        CommunityTag tag = communityTagRepository.findById(tagId)
            .orElseThrow(() -> new NotFoundException("태그를 찾을 수 없습니다."));

        // 상태 반전
        tag.setStatus(
            tag.getStatus() == CommunityTag.Status.ACTIVE
                ? CommunityTag.Status.INACTIVE
                : CommunityTag.Status.ACTIVE
        );
    }
}
