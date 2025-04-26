package com.highfive.meetu.domain.community.admin.dto;

import com.highfive.meetu.domain.community.common.entity.CommunityTag;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class CommunityTagDTO {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer status;

    public static CommunityTagDTO from(CommunityTag tag) {
        return CommunityTagDTO.builder()
            .id(tag.getId())
            .name(tag.getName())
            .createdAt(tag.getCreatedAt())
            .updatedAt(tag.getUpdatedAt())
            .status(tag.getStatus())
            .build();
    }
}