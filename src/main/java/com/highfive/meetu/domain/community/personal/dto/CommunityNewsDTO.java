package com.highfive.meetu.domain.community.personal.dto;

import lombok.*;

/**
 * 뉴스 조회 결과 DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityNewsDTO {

  private String title;        // 뉴스 제목
  private String description;  // 뉴스 요약 내용
  private String url;          // 뉴스 원문 링크
  private String publishedAt;  // 발행 일시
  private String sourceName;   // 언론사 이름
}
