package com.highfive.meetu.domain.community.personal.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.highfive.meetu.domain.community.personal.dto.CommunityNewsDTO;
import com.highfive.meetu.domain.community.personal.dto.CommunityPostDTO;
import com.highfive.meetu.domain.community.personal.dto.CommunityPostListDTO;
import com.highfive.meetu.domain.community.personal.dto.CommunityPostSimpleDTO;
import com.highfive.meetu.domain.community.personal.service.CommunityNewsService;
import com.highfive.meetu.domain.community.personal.service.CommunityPostService;
import com.highfive.meetu.global.common.exception.BadRequestException;
import com.highfive.meetu.global.common.response.ResultData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/personal/community/posts")
@RequiredArgsConstructor
public class CommunityPostController {

  private final CommunityPostService communityPostService;
  private final CommunityNewsService communityNewsService;

  //커뮤니티 게시글 등록
  @PostMapping("/create")
  public ResultData<CommunityPostDTO> createPost(
      @RequestPart("data") CommunityPostDTO dto,
      @RequestPart(value = "image", required = false) MultipartFile image
  ) {
    CommunityPostDTO result = communityPostService.createPost(dto, image);
    return ResultData.success(1, result);
  }

  //파싱 전 코드
/*  public ResultData<CommunityPostDTO> createPost(
      @RequestPart("data") CommunityPostDTO dto,
      @RequestPart(value = "image", required = false) MultipartFile image
  ) {
    CommunityPostDTO result = communityPostService.createPost(dto, image);
    return ResultData.success(1, result);
  }*/


  // 커뮤니티 게시글 목록 조회 (최신순, 단건조회)
  @GetMapping("/all-posts")
  public ResultData<Map<String, Object>> getAllActivePosts() {
    List<CommunityPostDTO> posts = communityPostService.getAllActivePosts();

    // 기본 기간: 최근 7일로 설정
    LocalDate now = LocalDate.now();
    LocalDate from = now.minusDays(7);
    LocalDate to = now;


    // tagId가 null이면 기본값 1 (면접) 사용
    List<CommunityNewsDTO> news = communityNewsService.getNewsByTagId(1L, from, to);
    System.out.println("news: " + news);

    // posts + news 를 하나의 Map으로 묶음
    Map<String, Object> response = new HashMap<>();
    response.put("posts", posts);
    response.put("news", news);

    return ResultData.success(posts.size(), response);
  }

  // 커뮤니티 게시글 목록 조회 (최신순, 리스트로 목록조회)
  @GetMapping("/list")
  public ResultData<List<CommunityPostListDTO>> getPostList(
      @RequestParam(defaultValue = "7") int limit
  ) {
    List<CommunityPostListDTO> posts = communityPostService.getPostListWithWriterAndTag(limit);
    return ResultData.success(posts.size(), posts);
  }


  // 해시태그별 게시물 조회 (최신순)
  @GetMapping("/tag/{tagId}")
  public ResultData<List<CommunityPostDTO>> getPostsByTag(@PathVariable Long tagId) {
    List<CommunityPostDTO> result = communityPostService.getPostsByTagId(tagId);
    return ResultData.success(result.size(), result);
  }

  // 게시글 상세 조회
  @GetMapping("/detail/{postId}")
  public ResultData<CommunityPostDTO> getPostDetail(@PathVariable Long postId) {
    CommunityPostDTO result = communityPostService.getPostDetail(postId);
    return ResultData.success(1, result);
  }

  // 게시글 수정
  @PostMapping("/edit")
  public ResultData<CommunityPostDTO> updatePost(
      @RequestPart("data") CommunityPostDTO dto, // JSON 부분
      @RequestPart(value = "image", required = false) MultipartFile image // 이미지 파일
  ) {
    System.out.println("받은 existingImageUrl: " + dto.getExistingImageUrl());

    CommunityPostDTO result = communityPostService.updatePost(dto, image);
    return ResultData.success(1, result);
  }


  //게시글 삭제 (Soft Delete)
  @PostMapping("/delete/{postId}")
  public ResultData<String> deletePost(@PathVariable Long postId,
                                       @RequestBody Map<String, Long> body) {
    Long accountId = body.get("accountId");
    communityPostService.deletePost(postId, accountId);
    return ResultData.success(1, "게시글이 삭제되었습니다.");
  }


  // 인기글 조회 (각 태그당 1개씩 뽑아와서 보여주는 기준)
  @GetMapping("/popular")
  public ResultData<List<CommunityPostSimpleDTO>> getPopularPosts(
      @RequestParam(defaultValue = "7") int limit
  ) {
    List<CommunityPostSimpleDTO> result = communityPostService.getPopularPosts(limit);
    return ResultData.success(result.size(), result);
  }

  // 인기글 조회 (각 해시태그 기준)
  @GetMapping("/popular/tag/{tagId}")
  public ResultData<List<CommunityPostSimpleDTO>> getPopularPostsByTag(
      @PathVariable Long tagId,
      @RequestParam(defaultValue = "7") int limit
  ) {
    List<CommunityPostSimpleDTO> result = communityPostService.getPopularPostsByTag(tagId, limit);
    return ResultData.success(result.size(), result);
  }

  // 내가 쓴 글 조회
  @GetMapping("/my/{accountId}")
  public ResultData<List<CommunityPostDTO>> getMyPosts(@PathVariable Long accountId) {
    List<CommunityPostDTO> result = communityPostService.getMyPosts(accountId);
    return ResultData.success(result.size(), result);
  }



}
