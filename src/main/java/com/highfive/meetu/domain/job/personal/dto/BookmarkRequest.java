package com.highfive.meetu.domain.job.personal.dto;

import lombok.*;

/**
 * 공고 스크랩(북마크) 요청 DTO
 * - 클라이언트로부터 jobPostingId를 전달받기 위한 용도
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkRequest {
    private Long jobPostingId; // 스크랩/스크랩 해제할 채용 공고 ID
}
