package com.highfive.meetu.domain.community.personal.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 커뮤니티 게시글 목록 조회 DTO
 * - 게시글, 작성자, 해시태그, 이미지 정보를 포함
 * - 프론트에서 게시글 리스트를 출력할 때 사용
 */
@Getter
public class CommunityPostListDTO {

  private Long postId;             // 게시글 ID
  private String title;            // 게시글 제목
  private String content;          // 게시글 내용
  private Long tagId;              // 해시태그 ID
  private String tagName;          // 해시태그 이름
  private Long accountId;          // 작성자 계정 ID
  private String name;         // 작성자 닉네임
  private String postImageUrl;     // Presigned 이미지 URL
  private Integer likeCount;       // 좋아요 수
  private Integer commentCount;    // 댓글 수
  private LocalDateTime createdAt; // 작성일시
  private String profileImageUrl; // 작성자 프로필 이미지 Url

  /**
   * QueryDSL에서 직접 조회할 수 있도록 생성자에 @QueryProjection 추가
   * - postImageKey는 URL 변환을 위해 받아와서 postImageUrl에 매핑
   */
  @QueryProjection
  public CommunityPostListDTO(
      Long postId,
      String title,
      String content,
      Long tagId,
      String tagName,
      Long accountId,
      String name,
      String postImageKey, // key를 받아서 나중에 URL로 변환
      Integer likeCount,
      Integer commentCount,
      LocalDateTime createdAt,
      String profileImageUrl
  ) {
    this.postId = postId;
    this.title = title;
    this.content = content;
    this.tagId = tagId;
    this.tagName = tagName;
    this.accountId = accountId;
    this.name = name;
    this.postImageUrl = postImageKey; // 이후 S3Service에서 URL로 변환 예정
    this.likeCount = likeCount;
    this.commentCount = commentCount;
    this.createdAt = createdAt;
    this.profileImageUrl = profileImageUrl;
  }
}
