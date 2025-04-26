package com.highfive.meetu.domain.community.personal.controller;

import com.highfive.meetu.domain.community.personal.dto.CommunityTagDTO;
import com.highfive.meetu.domain.community.personal.service.CommunityTagService;
import com.highfive.meetu.global.common.response.ResultData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/community/tags")
@RequiredArgsConstructor
public class CommunityTagController {

  private final CommunityTagService communityTagService;

// 사용가능한 커뮤니티 해시태그 목록 전체를 조회함
  @GetMapping("/active-tag")
  public ResultData<List<CommunityTagDTO>> getTags() {
    List<CommunityTagDTO> tagList = communityTagService.getActiveTags();
    return ResultData.success(tagList.size(), tagList);
  }
}
