package com.highfive.meetu.domain.job.personal.controller;

import com.highfive.meetu.domain.job.personal.dto.BookmarkRequest;
import com.highfive.meetu.domain.job.personal.service.BookmarkService;
import com.highfive.meetu.global.common.response.ResultData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 채용 공고 스크랩(북마크) API 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/personal/bookmark")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    /**
     * 공고 스크랩 등록
     *
     * @param request 스크랩할 공고 ID를 담은 요청 객체
     * @return 등록 성공 메시지
     */
    @PostMapping("/add")
    public ResultData<?> bookmark(@RequestBody BookmarkRequest request) {
        bookmarkService.bookmark(request.getJobPostingId());
        return ResultData.success(1, "공고 스크랩 완료");
    }

    /**
     * 공고 스크랩 해제
     *
     * @param request 해제할 공고 ID를 담은 요청 객체
     * @return 해제 성공 메시지
     */
    @PostMapping("/remove")
    public ResultData<?> unbookmark(@RequestBody BookmarkRequest request) {
        bookmarkService.unbookmark(request.getJobPostingId());
        return ResultData.success(1, "공고 스크랩 해제 완료");
    }
}
