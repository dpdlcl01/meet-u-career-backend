package com.highfive.meetu.domain.community.admin.dto;

import lombok.*;

/**
 * 댓글 삭제 요청 DTO
 */
@Getter
@Setter
public class CommentDeleteRequestDTO {
    private Long commentId; // 삭제할 댓글 ID
}
