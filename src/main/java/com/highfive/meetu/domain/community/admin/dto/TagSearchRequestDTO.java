package com.highfive.meetu.domain.community.admin.dto;

import lombok.*;

@Getter
@Setter
public class TagSearchRequestDTO {
    private Integer status; // 0: 활성, 1: 비활성, null: 전체
    private int page = 0;
    private int size = 10;
    private String sortBy = "createdAt";
    private String direction = "DESC";
}
