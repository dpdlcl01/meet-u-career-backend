package com.highfive.meetu.domain.community.personal.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityPostSimpleDTO {
  private Long id;
  private String title;
  private int likeCount;
  private int commentCount;
  private LocalDateTime createdAt;
}
