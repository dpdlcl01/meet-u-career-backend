package com.highfive.meetu.domain.community.admin.dto;

import lombok.*;

/**
 * 커뮤니티 게시글 검색 요청 DTO
 */
@Getter
@Setter
public class CommunityPostSearchRequestDTO {
    private Integer status;        // 게시글 상태 (null: 전체)
    private int page = 0;          // 페이지 번호
    private int size = 10;         // 페이지 크기
    private String sortBy = "createdAt";  // 정렬 기준
    private String direction = "DESC";    // 정렬 방향 (DESC / ASC)
}