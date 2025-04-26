package com.highfive.meetu.domain.community.personal.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

/**
  * 작성,수정,단건 조회용 DTO
 */

public class CommunityPostDTO {

  private Long id; // 게시글 ID
  private Long accountId; // 작성자 계정 ID
  private Long tagId; // 선택한 해시태그 ID
  private String title; // 게시글 제목
  private String content; // 게시글 본문
  private String postImageKey; // S3에 저장된 대표 이미지 파일의 Key (예: communityPost/post123_abc.jpg)
  private String postImageUrl; // postImageKey 를 Url로 변경해주어야야 프론트에서 출력이 가능
  private String profileImageUrl; // 작성자 프로필 이미지 Presigned URL 추가
  private String existingImageUrl; // 기존 게시글 수정 시, 새 이미지 업로드 없이 기존 이미지를 유지할 때 사용하는 Presigned URL
  private Integer likeCount; // 좋아요 수
  private Integer commentCount; // 댓글 수
  private Integer status; // 게시글 상태 (0: 게시 중, 1: 삭제됨)
  private LocalDateTime createdAt; // 게시글 생성일
  private LocalDateTime updatedAt; // 게시글 수정일


}



