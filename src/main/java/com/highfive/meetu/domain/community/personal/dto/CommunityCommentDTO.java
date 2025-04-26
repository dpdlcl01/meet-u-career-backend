package com.highfive.meetu.domain.community.personal.dto;

import com.highfive.meetu.domain.community.common.entity.CommunityComment;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityCommentDTO {

  private Long id; // 댓글 ID
  private Long postId; // 댓글이 속한 게시글 ID
  private Long accountId; // 댓글 작성자 계정 ID
  private String authorName;   // 댓글 작성자 이름
  private String authorAvatar; // 댓글 작성자 프로필 URL
  private String content; // 댓글 내용
  private Integer status; // 댓글 상태 (0: 정상, 1: 삭제됨)
  private LocalDateTime createdAt; // 댓글 생성일
  private LocalDateTime updatedAt; // 댓글 수정일



  public static CommunityCommentDTO fromEntity(CommunityComment entity) {
    return CommunityCommentDTO.builder()
        .id(entity.getId())
        .postId(entity.getPost().getId())
        .accountId(entity.getAccount().getId())
        .authorName(entity.getAccount().getName())
        .authorAvatar(entity.getAccount().getProfile() != null
            ? entity.getAccount().getProfile().getProfileImageKey()
            : "/profile.png")
        .content(entity.getContent())
        .status(entity.getStatus())
        .createdAt(entity.getCreatedAt())
        .updatedAt(entity.getUpdatedAt())
        .build();
  }

}



