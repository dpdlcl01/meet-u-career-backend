package com.highfive.meetu.domain.company.admin.dart;


import com.highfive.meetu.domain.company.admin.dart.ParsedCorp;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * DART Open DART API를 통해 기업 코드 목록을 다운로드하고,
 * JSON 파일로 저장/로드하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DartCorpCodeService {

  @Value("${api.dart.apiKey}")
  private String dartApiKey;

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper = new ObjectMapper();

  private static final String ZIP_URL = "https://opendart.fss.or.kr/api/corpCode.xml?crtfc_key=%s";
  private static final String OUTPUT_PATH = "src/main/resources/dart-corp-list.json";

  /**
   * DART에서 ZIP 파일을 받아 파싱 후 JSON으로 저장
   */
  public void fetchAndSaveCorpList() {
    String url = String.format(ZIP_URL, dartApiKey);
    try {
      // 1) ZIP 파일 다운로드
      byte[] zipBytes = restTemplate.getForObject(url, byte[].class);
      if (zipBytes == null) {
        log.error("DART API로부터 ZIP 데이터를 가져오지 못했습니다.");
        return;
      }

      // 2) ZIP 풀기 및 CORPCODE.xml 찾기
      try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
          if ("CORPCODE.xml".equalsIgnoreCase(entry.getName())) {
            // 3) XML 파싱
            Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(zis);
            doc.getDocumentElement().normalize();

            NodeList listNodes = doc.getElementsByTagName("list");
            List<ParsedCorp> parsed = new ArrayList<>(listNodes.getLength());

            for (int i = 0; i < listNodes.getLength(); i++) {
              Node node = listNodes.item(i);
              String corpCode = null, corpName = null, modifyDate = null;
              NodeList children = node.getChildNodes();
              for (int j = 0; j < children.getLength(); j++) {
                Node c = children.item(j);
                String nodeName = c.getNodeName();
                String text = c.getTextContent();
                if ("corp_code".equalsIgnoreCase(nodeName)) {
                  corpCode = text;
                } else if ("corp_name".equalsIgnoreCase(nodeName)) {
                  corpName = text;
                } else if ("modify_date".equalsIgnoreCase(nodeName)) {
                  modifyDate = text;
                }
              }
              if (corpCode != null && corpName != null && modifyDate != null) {
                parsed.add(new ParsedCorp(corpCode, corpName, modifyDate));
              }
            }

            // 4) JSON 직렬화 및 파일 저장
            byte[] jsonBytes = objectMapper.writeValueAsBytes(parsed);
            Files.write(Paths.get(OUTPUT_PATH), jsonBytes);
            log.info("DART 기업 코드 목록을 {}에 저장했습니다. (총 {}건)", OUTPUT_PATH, parsed.size());
            return;
          }
        }
        log.error("ZIP 파일에서 CORPCODE.xml을 찾을 수 없습니다.");
      }
    } catch (Exception e) {
      log.error("DART 기업 코드 목록 처리 중 오류 발생: {}", e.getMessage(), e);
    }
  }

  /**
   * 저장된 JSON 파일을 읽어 ParsedCorp 리스트로 반환
   */
  public List<ParsedCorp> loadParsedCorps() {
    try {
      byte[] jsonBytes = Files.readAllBytes(Paths.get(OUTPUT_PATH));
      return objectMapper.readValue(jsonBytes, new TypeReference<List<ParsedCorp>>() {});
    } catch (Exception e) {
      log.error("dart-corp-list.json 로드 중 오류 발생: {}", e.getMessage(), e);
      return List.of();
    }
  }
}