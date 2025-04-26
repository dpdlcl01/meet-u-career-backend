package com.highfive.meetu.domain.community.personal.controller;

import com.highfive.meetu.domain.community.personal.dto.CommunityNewsDTO;
import com.highfive.meetu.domain.community.personal.service.CommunityNewsService;
import com.highfive.meetu.global.common.response.ResultData;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/community/news")
@RequiredArgsConstructor
public class CommunityNewsController {

  private final CommunityNewsService communityNewsService;

  // 태그 id로 해시태그를 받아와서, 해시태그 키워드로 뉴스목록을 받아옴
  @GetMapping("/{tagId}")
  public ResultData<List<CommunityNewsDTO>> getNewsByTag(
      @PathVariable Long tagId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to

  ) {

    if (from == null || to == null) {
      LocalDate now = LocalDate.now();
      from = now.minusDays(30);
      to = now;
    }

    List<CommunityNewsDTO> result = communityNewsService.getNewsByTagId(tagId, from, to);
    return ResultData.success(result.size(), result);
  }
}