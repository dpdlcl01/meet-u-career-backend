package com.highfive.meetu.domain.company.admin.controller;

import com.highfive.meetu.domain.company.admin.dart.DartCorpCodeService;
import com.highfive.meetu.domain.company.admin.dart.ParsedCorp;
import com.highfive.meetu.domain.company.common.entity.Company;
import com.highfive.meetu.domain.company.common.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 관리자 전용: DART 기업 코드 업데이트용 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class CorpCodeUpdateController {

  private final DartCorpCodeService dartService;
  private final CompanyRepository companyRepo;

  /**
   * 기업 테이블의 corpCode 필드를 DART 데이터를 기반으로 업데이트
   *
   * @return 업데이트/스킵 카운트
   */
  @PostMapping("/update-corp-codes")
  public ResponseEntity<Map<String, Integer>> updateCorpCodes() {
    List<ParsedCorp> parsedList = dartService.loadParsedCorps();

    // corpCode가 없는 회사만 대상
    List<Company> targets = companyRepo.findAll().stream()
        .filter(c -> c.getCorpCode() == null)
        .collect(Collectors.toList());

    int updated = 0, skipped = 0;

    for (Company company : targets) {
      String normalized = company.getName()
          // DART API 데이터와 매핑 전 불필요 문자열 제거
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

      Optional<ParsedCorp> match = parsedList.stream()
          .filter(p -> p.getCorpName().equalsIgnoreCase(normalized))
          .findFirst();

      if (match.isPresent()) {
        company.setCorpCode(match.get().getCorpCode());
        companyRepo.save(company);
        updated++;
      } else {
        log.warn("일치하는 DART 기업 코드를 찾을 수 없어 스킵: 회사명='{}'", company.getName());
        skipped++;
      }
    }

    Map<String, Integer> result = new HashMap<>();
    result.put("updated", updated);
    result.put("skipped", skipped);
    return ResponseEntity.ok(result);
  }
}