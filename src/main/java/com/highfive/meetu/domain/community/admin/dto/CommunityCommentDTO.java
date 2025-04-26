package com.highfive.meetu.domain.community.admin.dto;

import com.highfive.meetu.domain.community.common.entity.CommunityComment;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 커뮤니티 댓글 DTO (관리자용)
 */
@Data
@Builder
public class CommunityCommentDTO {

    private Long id;
    private String content;
    private Integer status;
    private Long postId;
    private Long accountId;
    private String accountName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CommunityCommentDTO from(CommunityComment comment) {
        return CommunityCommentDTO.builder()
            .id(comment.getId())
            .content(comment.getContent())
            .status(comment.getStatus())
            .postId(comment.getPost().getId())
            .accountId(comment.getAccount().getId())
            .accountName(comment.getAccount().getName())
            .createdAt(comment.getCreatedAt())
            .updatedAt(comment.getUpdatedAt())
            .build();
    }
}
