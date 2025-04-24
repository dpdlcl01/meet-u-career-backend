package com.highfive.meetu.domain.community.personal.dto;

import lombok.*;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class CommunityLikeDTO {

  private Long id;             // 좋아요 ID
  private Long accountId;      // 사용자 ID
  private Long postId;         // 게시글 ID
  private LocalDateTime createdAt; // 좋아요 생성일

}

