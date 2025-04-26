package com.highfive.meetu.domain.community.admin.dto;

import com.highfive.meetu.domain.community.common.entity.CommunityPost;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 커뮤니티 게시글 응답 DTO
 */
@Getter
@Builder
public class CommunityPostDTO {

    private Long id;
    private String title;
    private String content;
    private Integer status;
    private String postImageKey;
    private int likeCount;
    private int commentCount;
    private Long accountId;
    private String accountName;
    private Long tagId;
    private String tagName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CommunityPostDTO from(CommunityPost post) {
        return CommunityPostDTO.builder()
            .id(post.getId())
            .title(post.getTitle())
            .content(post.getContent())
            .status(post.getStatus())
            .postImageKey(post.getPostImageKey())
            .likeCount(post.getLikeCount())
            .commentCount(post.getCommentCount())
            .accountId(post.getAccount().getId())
            .accountName(post.getAccount().getName())
            .tagId(post.getTag().getId())
            .tagName(post.getTag().getName())
            .createdAt(post.getCreatedAt())
            .updatedAt(post.getUpdatedAt())
            .build();
    }
}
