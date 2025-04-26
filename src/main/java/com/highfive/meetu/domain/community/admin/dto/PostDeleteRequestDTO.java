package com.highfive.meetu.domain.community.admin.dto;

import lombok.*;

/**
 * 게시글 삭제 요청 DTO
 */
@Getter
@Setter
public class PostDeleteRequestDTO {
    private Long postId; // 삭제할 게시글 ID
}
