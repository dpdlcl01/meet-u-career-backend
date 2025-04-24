package com.highfive.meetu.domain.community.personal.dto;


import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityTagDTO {

  private Long id; // 태그 ID
  private String name; // 태그 이름
  private Integer status; // 태그 상태 (0: 사용 중, 1: 비활성화)
  private LocalDateTime createdAt; // 태그 생성일
  private LocalDateTime updatedAt; // 태그 수정일


  // 태그 이름별로 검색 키워드를 반환하는 메서드
  public static List<String> getSearchKeywordsByTag(String tagName) {
    switch (tagName) {
      case "면접":
        return List.of("면접", "면접관", "면접 질문", "면접 후기");
      case "이직":
        return List.of("이직", "퇴사", "커리어 전환", "직장 변경");
      case "연봉":
        return List.of("연봉", "연봉협상", "연봉인상", "연봉평균");
      case "취업":
        return List.of("취업", "채용", "신입공채", "공기업");
      case "자기소개서":
        return List.of("자기소개서", "자소서", "자소서작성", "이력서");
      case "커리어":
        return List.of("커리어", "경력", "경력관리", "직무개발");
      case "자격증":
        return List.of("자격증", "국가자격", "기술자격", "자격시험");
      default:
        return List.of(tagName); // fallback
    }
  }
}