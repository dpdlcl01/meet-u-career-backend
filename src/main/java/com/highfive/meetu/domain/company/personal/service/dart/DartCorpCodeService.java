package com.highfive.meetu.domain.company.personal.dart;

import com.highfive.meetu.domain.company.common.entity.Company;
import com.highfive.meetu.domain.company.common.repository.CompanyRepository;
import com.highfive.meetu.global.common.exception.BadRequestException;
import com.highfive.meetu.global.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.*;
import jakarta.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * DART API에서 제공하는 corpCode.zip을 다운로드하여
 * Company.corpCode 필드를 업데이트하고,
 * 이름→코드 조회 메서드를 제공하는 서비스
 */
@Service
@RequiredArgsConstructor
public class DartCorpCodeService {

  private final CompanyRepository companyRepository;
  private final RestTemplate restTemplate = new RestTemplate();

  // DART API 키 (application-secret.yml)
  @Value("${api.dart.apiKey}")
  private String dartApiKey;

  /**
   * 애플리케이션 시작 후 최초 1회 실행
   */
  @PostConstruct
  public void init() {
    reloadCorpCodes();
  }

  /**
   * 매일 새벽 2시에 DART corpCode 재로딩
   */
  @Scheduled(cron = "0 0 2 * * *")
  public void scheduledReload() {
    reloadCorpCodes();
  }

  /**
   * DART API에서 corpCode.zip 다운로드 → XML 파싱 → Company.corpCode 업데이트
   */
  private void reloadCorpCodes() {
    try {
      // 1) ZIP 파일 다운로드
      String url = "https://opendart.fss.or.kr/api/corpCode.xml?crtfc_key=" + dartApiKey;
      byte[] zipBytes = restTemplate.getForObject(url, byte[].class);
      if (zipBytes == null) {
        throw new BadRequestException("DART API 응답이 없습니다.");
      }

      // 2) ZIP 해제 및 CORPCODE.xml 파싱
      try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
        ZipEntry entry;
        boolean found = false;

        while ((entry = zis.getNextEntry()) != null) {
          if ("CORPCODE.xml".equalsIgnoreCase(entry.getName())) {
            found = true;

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(zis);
            NodeList nodes = doc.getElementsByTagName("list");

            for (int i = 0; i < nodes.getLength(); i++) {
              Element el = (Element) nodes.item(i);
              String code = el.getElementsByTagName("corp_code").item(0).getTextContent();
              String name = el.getElementsByTagName("corp_name").item(0).getTextContent();

              // 3) 회사명으로 Company 조회 후 corpCode 업데이트
              companyRepository.findByName(name)
                  .ifPresent(company -> {
                    company.setCorpCode(code);
                    companyRepository.save(company);
                  });
            }
            break;
          }
        }

        if (!found) {
          throw new BadRequestException("ZIP 내부에 CORPCODE.xml을 찾을 수 없습니다.");
        }
      }

    } catch (BadRequestException e) {
      throw e;
    } catch (Exception e) {
      // I/O, XML, DB 오류 래핑
      throw new BadRequestException("corpCode 업데이트 실패: " + e.getMessage());
    }
  }

  /**
   * 회사명으로 8자리 corpCode 조회
   * @param corpName 회사명
   * @return 8자리 corpCode
   * @throws NotFoundException 등록되지 않은 회사명인 경우
   */
  public String getCorpCodeByName(String corpName) {
    return companyRepository.findByName(corpName)
        .map(Company::getCorpCode)
        .orElseThrow(() -> new NotFoundException("등록된 기업명이 아닙니다: " + corpName));
  }

}