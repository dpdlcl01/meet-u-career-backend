// src/main/java/com/highfive/meetu/domain/company/admin/service/Company2FinancialService.java
package com.highfive.meetu.domain.company.admin.service;

import com.highfive.meetu.domain.company.common.entity.Company;
import com.highfive.meetu.domain.company.common.repository.CompanyRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 관리자 전용 서비스
 * • DART API를 통한 재무정보·직원통계 업데이트
 * • 금융위원회_기업기본정보 API를 통한 기업 기본정보 업데이트
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class Company2FinancialService {

  private final CompanyRepository companyRepo;
  private final RestTemplate restTemplate;

  @Value("${api.dart.apiKey}")

  private String dartApiKey;

  @Value("${api.finance.key}")
  private String serviceKey;

  // (기존 updateFinancials, updateEmployeeStats 메서드는 생략)

  /**
   * 금융위원회_기업기본정보 API로부터
   * • 사업자등록번호(crno)
   * • 직원수(enpEmpeCnt)
   * • 평균 급여(enpPn1AvgSlryAmt)
   * 를 조회하여 Company 엔티티에 저장
   *
   * @return { "updated": 갱신건수, "skipped": 스킵건수 }
   */
  public Map<String, Integer> updateCorpBasicInfo() {
    List<Company> targets = companyRepo.findAll();
    int updatedCount = 0;
    int skippedCount = 0;

    for (Company company : targets) {
      String raw = null;
      // 1) 회사명 정규화
      String normalized = company.getName()
          .replaceAll("(?i)^\\(주\\)|\\(주\\)$", "")
          .replaceAll("(?i)주식회사", "")
          .replaceAll("(?i)투자회사", "")
          .replaceAll("(?i)유한회사", "")
          .replaceAll("(?i)합자회사", "")
          .replaceAll("(?i)합명회사", "")
          .replaceAll("(?i)유한책임회사", "")
          .replaceAll("(?i)사단법인", "")
          .replaceAll("(?i)재단법인", "")
          .replaceAll("(?i)학교법인", "")
          .replaceAll("(?i)사회복지법인", "")
          .replaceAll("(?i)의료법인", "")
          .replaceAll("(?i)회계법인", "")
          .replaceAll("(?i)텔레콤", "")
          .trim();

      try {
        // 2) Build URL: raw serviceKey, percent-encode only non-ASCII corpNm
        String encodedCorp = URLEncoder.encode(normalized, StandardCharsets.UTF_8.toString());
        String url = UriComponentsBuilder
            .fromUriString("http://apis.data.go.kr/1160100/service/GetCorpBasicInfoService_V2/getCorpOutline_V2")
            .queryParam("serviceKey", serviceKey)
            .queryParam("pageNo", 1)
            .queryParam("numOfRows", 1)
            .queryParam("resultType", "json")
            .queryParam("corpNm", encodedCorp)
            .build(false)
            .toUriString();
        System.out.println(url);
        // 3) API 호출: raw String 및 JSON 파싱
        log.info("Calling external API URL: {}", url);
        raw = restTemplate.getForObject(url, String.class);
        log.info("External API raw response: {}", raw);
        if (raw == null || raw.trim().startsWith("<")) {
          log.error("기업 기본정보 업데이트 실패: {} (ID={}), 원시 응답이 JSON이 아닙니다. raw:\n{}", company.getName(), company.getId(),
              raw);
          skippedCount++;
          continue;
        }
        JsonNode root = new ObjectMapper().readTree(raw);
        JsonNode item = root.path("response").path("body").path("items").path("item").get(0);

        // 4) 필드 추출
        String crno = item.path("crno").asText();
        String enpEmpeCnt = item.path("enpEmpeCnt").asText().replaceAll(",", "");
        String avgSalary = item.path("enpPn1AvgSlryAmt").asText().replaceAll(",", "");

        // 5) 엔티티 업데이트 및 저장
        company.setLogoKey(crno);
        company.setNumEmployees(Integer.parseInt(enpEmpeCnt));
        company.setLogoUrl(avgSalary);
        companyRepo.save(company);

        updatedCount++;

      } catch (Exception e) {
        // 오류 발생 시 스킵하고 로그만 남김
        log.error("기업 기본정보 업데이트 실패: {} (ID={}), 원인={}",
            company.getName(), company.getId(), e.getMessage());
        skippedCount++;
      }
    }

    // 결과 맵 생성
    Map<String, Integer> result = new HashMap<>();
    result.put("updated", updatedCount);
    result.put("skipped", skippedCount);

    log.info("기업 기본정보 업데이트 완료: updated={}, skipped={}", updatedCount, skippedCount);
    return result;
  }
}