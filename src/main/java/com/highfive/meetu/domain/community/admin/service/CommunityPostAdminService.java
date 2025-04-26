package com.highfive.meetu.domain.community.admin.service;

import com.highfive.meetu.domain.community.admin.dto.CommunityPostDTO;
import com.highfive.meetu.domain.community.admin.dto.CommunityPostSearchRequestDTO;
import com.highfive.meetu.domain.community.common.entity.CommunityPost;
import com.highfive.meetu.domain.community.common.repository.CommunityPostRepository;
import com.highfive.meetu.global.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.highfive.meetu.global.common.response.ErrorCode.POST_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CommunityPostAdminService {

    private final CommunityPostRepository communityPostRepository;

    /**
     * 게시글 목록 조회 (상태 필터링 + 페이징)
     */
    public Page<CommunityPostDTO> searchPosts(CommunityPostSearchRequestDTO request) {
        Sort.Direction direction = Sort.Direction.fromString(request.getDirection());
        Sort sort = Sort.by(direction, request.getSortBy());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page<CommunityPost> page = (request.getStatus() == null)
            ? communityPostRepository.findAll(pageable)
            : communityPostRepository.findAllByStatus(request.getStatus(), pageable);

        return page.map(CommunityPostDTO::from);
    }

    /**
     * 게시글 전체 조회 (페이징 없이)
     */
    public List<CommunityPostDTO> findAllPosts() {
        return communityPostRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
            .stream()
            .map(CommunityPostDTO::from)
            .toList();
    }

    /**
     * 게시글 상세 조회
     */
    public CommunityPostDTO getPostDetail(Long postId) {
        CommunityPost post = communityPostRepository.findById(postId)
            .orElseThrow(() -> new NotFoundException(POST_NOT_FOUND.getMessage()));

        return CommunityPostDTO.from(post);
    }

    /**
     * 게시글 상태 토글 (0 <-> 1)
     */
    @Transactional
    public void togglePostStatus(Long postId) {
        CommunityPost post = communityPostRepository.findById(postId)
            .orElseThrow(() -> new NotFoundException(POST_NOT_FOUND.getMessage()));

        if (post.getStatus() == CommunityPost.Status.ACTIVE) {
            post.setStatus(CommunityPost.Status.DELETED);
        } else {
            post.setStatus(CommunityPost.Status.ACTIVE);
        }

        communityPostRepository.save(post);
    }

    /**
     * 게시글 삭제 처리 (Status 변경)
     * @param postId 삭제할 게시글 ID
     */
    @Transactional
    public void deletePost(Long postId) {
        // 1. 게시글 조회
        CommunityPost post = communityPostRepository.findById(postId)
            .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다."));

        // 2. 게시글 상태 변경
        post.setStatus(CommunityPost.Status.DELETED);
    }
}
