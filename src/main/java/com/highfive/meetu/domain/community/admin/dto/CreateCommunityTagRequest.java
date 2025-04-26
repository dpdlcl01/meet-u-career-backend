package com.highfive.meetu.domain.community.admin.dto;

import lombok.*;

/**
 * 커뮤니티 태그 생성 요청 DTO
 */
@Getter
@Setter
public class CreateCommunityTagRequest {
    private String name;
    private int status;
}
