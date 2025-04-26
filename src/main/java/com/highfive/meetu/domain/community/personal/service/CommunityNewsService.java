package com.highfive.meetu.domain.community.personal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.highfive.meetu.domain.community.common.entity.CommunityTag;
import com.highfive.meetu.domain.community.common.repository.CommunityTagRepository;
import com.highfive.meetu.domain.community.personal.dto.CommunityNewsDTO;
import com.highfive.meetu.domain.community.personal.dto.CommunityTagDTO;
import com.highfive.meetu.domain.community.personal.dto.NewsApiResponse;
import com.highfive.meetu.domain.community.personal.dto.NewsArticle;
import com.highfive.meetu.global.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityNewsService {

  private final CommunityTagRepository communityTagRepository;

  @Value("${api.newsapi.key}")
  private String newsApiKey;

  // private final RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
  private final RestTemplate restTemplate = new RestTemplate();
  private final ObjectMapper objectMapper = new ObjectMapper();

  public List<CommunityNewsDTO> getNewsByTagId(Long tagId, LocalDate from, LocalDate to) {
    // 해시태그 조회
    CommunityTag tag = communityTagRepository.findById(tagId)
        .orElseThrow(() -> new NotFoundException("해시태그 정보를 찾을 수 없습니다."));

    // 태그 이름에 맞는 키워드 리스트 가져오기
    List<String> keywords = CommunityTagDTO.getSearchKeywordsByTag(tag.getName());
    String query = String.join(" OR ", keywords);

    System.out.println("검색 키워드: " + keywords);

    // 뉴스 API URL 생성
    String url = "https://newsapi.org/v2/everything?q=" + UriUtils.encode(query, StandardCharsets.UTF_8)
        + "&from=" + from.toString()
        + "&to=" + to.toString()
        + "&pageSize=5&sortBy=publishedAt&language=ko&apiKey=" + newsApiKey;


    System.out.println("호출 URL: " + url);

    // 헤더 설정
    HttpHeaders headers = new HttpHeaders();
    headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
    headers.set("Accept", "application/json");
    headers.set("Accept-Encoding", "gzip, deflate, br");


    HttpEntity<Void> entity = new HttpEntity<>(headers);

    // 원본 JSON 응답 받기 및 디버깅
    try {
      // 먼저 String으로 응답 받기
      ResponseEntity<String> rawResponse = restTemplate.exchange(
          url, HttpMethod.GET, entity, String.class
      );

      // 원본 응답 로깅
      System.out.println("응답 상태 코드: " + rawResponse.getStatusCode());
      System.out.println("응답 헤더: " + rawResponse.getHeaders());
      System.out.println("원본 응답 내용: " + rawResponse.getBody());

      // 응답이 비어있는지 확인
      if (rawResponse.getBody() == null || rawResponse.getBody().isEmpty()) {
        System.out.println("API 응답이 비어있습니다.");
        return new ArrayList<>();
      }

      // ObjectMapper로 직접 매핑 시도
      NewsApiResponse response = objectMapper.readValue(rawResponse.getBody(), NewsApiResponse.class);
      System.out.println("매핑된 응답 객체: " + response);

      // 기사 추출 및 DTO 변환
      List<NewsArticle> articles = response.getArticles();
      if (articles == null || articles.isEmpty()) {
        System.out.println("검색 결과가 없습니다.");
        return new ArrayList<>();
      }

      List<CommunityNewsDTO> newsList = new ArrayList<>();
      for (NewsArticle article : articles) {
        String sourceName = article.getSource() != null ? article.getSource().getName() : "알 수 없음";

        newsList.add(CommunityNewsDTO.builder()
            .title(article.getTitle())
            .description(article.getDescription())
            .url(article.getUrl())
            .publishedAt(article.getPublishedAt())
            .sourceName(sourceName)
            .build());
      }

      return newsList;

    } catch (Exception e) {
      System.err.println("API 호출 또는 응답 처리 중 오류 발생: " + e.getMessage());
      e.printStackTrace();
      throw new RuntimeException("뉴스 API 요청 실패: " + e.getMessage());
    }
  }
}