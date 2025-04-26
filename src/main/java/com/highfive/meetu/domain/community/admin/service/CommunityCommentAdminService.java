package com.highfive.meetu.domain.community.admin.service;

import com.highfive.meetu.domain.community.admin.dto.CommentSearchRequestDTO;
import com.highfive.meetu.domain.community.admin.dto.CommunityCommentDTO;
import com.highfive.meetu.domain.community.common.entity.CommunityComment;
import com.highfive.meetu.domain.community.common.repository.CommunityCommentRepository;
import com.highfive.meetu.global.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityCommentAdminService {

    private final CommunityCommentRepository communityCommentRepository;

    /**
     * 전체 댓글 조회 (status 조건 포함)
     */
    public Page<CommunityCommentDTO> getAllComments(CommentSearchRequestDTO request) {
        Sort.Direction direction = Sort.Direction.fromString(request.getDirection());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by(direction, request.getSortBy()));

        Page<CommunityComment> page = (request.getStatus() == null)
            ? communityCommentRepository.findAll(pageable)
            : communityCommentRepository.findAllByStatus(request.getStatus(), pageable);

        return page.map(CommunityCommentDTO::from);
    }

    /**
     * 전체 댓글 조회 (페이징 없이)
     */
    public List<CommunityCommentDTO> findAllComments() {
        return communityCommentRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
            .stream()
            .map(CommunityCommentDTO::from)
            .toList();
    }

    /**
     * 댓글 상세 조회
     */
    public CommunityCommentDTO getCommentDetail(Long commentId) {
        CommunityComment comment = communityCommentRepository.findById(commentId)
            .orElseThrow(() -> new NotFoundException("댓글을 찾을 수 없습니다."));
        return CommunityCommentDTO.from(comment);
    }

    /**
     * 댓글 상태 토글
     */
    @Transactional
    public void toggleStatus(Long commentId) {
        CommunityComment comment = communityCommentRepository.findById(commentId)
            .orElseThrow(() -> new NotFoundException("댓글을 찾을 수 없습니다."));

        comment.setStatus(comment.getStatus() == CommunityComment.Status.ACTIVE
            ? CommunityComment.Status.DELETED
            : CommunityComment.Status.ACTIVE);
    }

    /**
     * 댓글 삭제 처리 (Status 변경)
     * @param commentId 삭제할 댓글 ID
     */
    @Transactional
    public void deleteComment(Long commentId) {
        CommunityComment comment = communityCommentRepository.findById(commentId)
            .orElseThrow(() -> new NotFoundException("댓글을 찾을 수 없습니다."));

        comment.setStatus(CommunityComment.Status.DELETED);
    }
}
