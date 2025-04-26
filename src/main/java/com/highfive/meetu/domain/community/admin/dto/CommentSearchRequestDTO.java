package com.highfive.meetu.domain.community.admin.dto;

import lombok.*;

@Getter
@Setter
public class CommentSearchRequestDTO {
    private Integer status;       // 댓글 상태 (0: 활성, 1: 삭제)
    private int page = 0;         // 기본 0페이지
    private int size = 10;        // 기본 페이지당 10개
    private String sortBy = "createdAt";
    private String direction = "DESC";  // DESC or ASC
}
