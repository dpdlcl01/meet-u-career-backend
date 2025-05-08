
// src/main/java/com/highfive/meetu/domain/company/admin/service/CompanyFinancialService.java
package com.highfive.meetu.domain.company.admin.service;

import com.highfive.meetu.domain.company.common.entity.Company;
import com.highfive.meetu.domain.company.common.repository.CompanyRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 관리자 전용 서비스
 * DART API를 통해 기업 재무정보(매출액, 당기순이익) 및 직원통계(직원수, 평균급여)를
 * 주기적으로 업데이트합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyFinancialService {

  private final CompanyRepository companyRepo;

  @Value("${api.dart.apiKey}")
  private String dartApiKey;

  private final RestTemplate restTemplate;

  /**
   * 지정 연도(year), 보고서 코드(reprtCode)로
   * 매출액(revenue) 및 당기순이익(operatingProfit)을 업데이트합니다.
   *
   * @param year      조회 연도 (예: 2024)
   * @param reprtCode 보고서 코드 11011 (예: 1=1분기, 4=사업보고서)
   * @return 업데이트 및 스킵 카운트 맵
   */
  public Map<String, Integer> updateFinancials(int year, String reprtCode) {
    List<Company> companies = companyRepo.findAll().stream()
        .filter(c -> c.getCorpCode() != null)
        .toList();

    int updated = 0;
    int skipped = 0;

    for (Company company : companies) {
      String url = String.format(
          "https://opendart.fss.or.kr/api/fnlttSinglAcnt.json" +
              "?crtfc_key=%s&corp_code=%s&bsns_year=%d&reprt_code=%s",
          dartApiKey, company.getCorpCode(), year, reprtCode);

      try {
        JsonNode root = restTemplate.getForObject(url, JsonNode.class);
        if (root == null || !root.has("list")) {
          log.warn("재무정보 응답 누락: {} (ID={})", company.getName(), company.getId());
          skipped++;
          continue;
        }

        JsonNode list = root.get("list");
        long revenue = 0L;
        long profit = 0L;

        // list 배열 순회하며 매출액·당기순이익 추출
        for (JsonNode node : list) {
          String acctName = node.path("account_nm").asText();
          String amtStr = node.path("thstrm_amount").asText().replaceAll(",", "");
          if ("매출액".equals(acctName)) {
            revenue = Long.parseLong(amtStr);
          } else if ("당기순이익".equals(acctName)) {
            profit = Long.parseLong(amtStr);
          }
        }

        // 유효 데이터가 있을 때만 저장
        if (revenue != 0L || profit != 0L) {
          company.setRevenue(revenue);
          company.setOperatingProfit(profit);
          companyRepo.save(company);
          updated++;
        } else {
          log.warn("재무정보 항목 없음: {} (ID={})", company.getName(), company.getId());
          skipped++;
        }

      } catch (Exception e) {
        log.error("재무정보 업데이트 오류: {} (ID={}), 원인={}",
            company.getName(), company.getId(), e.getMessage());
        skipped++;
      }
    }

    Map<String, Integer> result = new HashMap<>();
    result.put("updated", updated);
    result.put("skipped", skipped);
    log.info("재무정보 업데이트 완료: updated={}, skipped={}", updated, skipped);
    return result;
  }

  /**
   * 지정 연도(year), 보고서 코드(reprtCode)로
   * 직원 수(numEmployees) 및 평균 급여(jan_salary_am)를 업데이트합니다.
   *
   * @param year      조회 연도 (예: 2024)
   * @param reprtCode 보고서 코드 11011 (예: 1=1분기, 4=사업보고서)
   * @return 업데이트 및 스킵 카운트 맵
   */
  public Map<String, Integer> updateEmployeeStats(int year, String reprtCode) {
    List<Company> companies = companyRepo.findAll().stream()
        .filter(c -> c.getCorpCode() != null)
        .toList();

    int updated = 0;
    int skipped = 0;

    for (Company company : companies) {
      String url = String.format(
          "https://opendart.fss.or.kr/api/empSttus.json" +
              "?crtfc_key=%s&corp_code=%s&bsns_year=%d&reprt_code=%s",
          dartApiKey, company.getCorpCode(), year, reprtCode);

      try {
        JsonNode root = restTemplate.getForObject(url, JsonNode.class);
        if (root == null || !root.has("list")) {
          log.warn("직원통계 응답 누락: {} (ID={})", company.getName(), company.getId());
          skipped++;
          continue;
        }

        JsonNode list = root.get("list");
        if (!list.isArray() || list.isEmpty()) {
          log.warn("직원통계 목록 비어있음: {} (ID={})", company.getName(), company.getId());
          skipped++;
          continue;
        }

        // 배열의 첫 번째 요소만 처리
        JsonNode node = list.get(0);
        String smText = node.path("sm").asText().replaceAll(",", "");
        String avgSalary = node.path("jan_salary_am").asText().replaceAll(",", "");

        boolean changed = false;
        if (!smText.isBlank()) {
          company.setNumEmployees(Integer.parseInt(smText));
          changed = true;
        }
        if (!avgSalary.isBlank()) {
          company.setAvgAnnualSalary(Long.valueOf(avgSalary));
          changed = true;
        }

        if (changed) {
          companyRepo.save(company);
          updated++;
        } else {
          log.warn("직원통계 항목 없음: {} (ID={})", company.getName(), company.getId());
          skipped++;
        }

      } catch (Exception e) {
        log.error("직원통계 업데이트 오류: {} (ID={}), 원인={}",
            company.getName(), company.getId(), e.getMessage());
        skipped++;
      }
    }

    Map<String, Integer> result = new HashMap<>();
    result.put("updated", updated);
    result.put("skipped", skipped);
    log.info("직원통계 업데이트 완료: updated={}, skipped={}", updated, skipped);
    return result;
  }
}