package com.highfive.meetu.domain.job.admin.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.highfive.meetu.domain.company.common.entity.Company;
import com.highfive.meetu.domain.company.common.repository.CompanyRepository;
import com.highfive.meetu.domain.job.common.entity.JobPosting;
import com.highfive.meetu.domain.job.common.repository.JobPostingRepository;
import com.highfive.meetu.domain.job.common.entity.Location;
import com.highfive.meetu.domain.job.common.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobPostingService {

    private final JobPostingRepository jobPostingRepo;
    private final ObjectMapper objectMapper;
    private final CompanyRepository companyRepository;
    private final LocationRepository locationRepository;

    // 사람인 API와 금융위원회 API 인증키
    @Value("${api.saramin.key}")
    private String saraminKey;

    @Value("${api.finance.key}")
    private String financeKey;

    // 사람인 API를 통해 채용공고 데이터를 가져와 DB에 저장
    public void updateJobPostings() {
        try {
            String keyword = "서비스업"; // 테스트용 키워드
            String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
            String apiUrl = "https://oapi.saramin.co.kr/job-search?access-key=" + saraminKey
                    + "&keywords=" + encodedKeyword
                    + "&output=json"
                    + "&fields=count"  // read-cnt, apply-cnt 포함
                    + "&count=100";     // 10개 출력

            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                responseBuilder.append(line);
            }
            br.close();

            System.out.println("사람인 API 응답: " + responseBuilder.toString());

            JsonNode jobsArray = objectMapper.readTree(responseBuilder.toString())
                    .path("jobs").path("job");

            for (JsonNode job : jobsArray) {
                // 채용공고의 jobId 추출
                String jobId = job.path("id").asText();

                // 중복된 채용공고는 건너뜁니다.
                Optional<JobPosting> existingPosting = jobPostingRepo.findByJobId(jobId);
                if (existingPosting.isPresent()) {
//                    System.out.println("중복된 공고 건너뜀: " + jobId);
                    continue;
                }

                // 기업명 추출
                String companyName = job.path("company").path("detail").path("name").asText();
                String industry = job.path("position").path("industry").path("name").asText();
                System.out.println("처리할 기업명: " + companyName);

                // 금융위원회 API 호출 후 Company 저장 또는 기존 정보 조회
                Company company = callCompanyInfo(companyName, industry);
                if (company == null) {
//                    System.out.println("회사 정보를 찾을 수 없습니다.: " + companyName);
                    continue;
                }

                // 위치 정보 처리 (첫 번째 지역 코드 사용)
                String locationCodeRaw = job.path("position").path("location").path("code").asText();
                String primaryLocationCode = locationCodeRaw.split(",")[0];

                Location location = locationRepository.findByLocationCode(primaryLocationCode)
                        .orElseThrow(() -> new IllegalArgumentException("해당 지역 코드가 존재하지 않습니다: " + primaryLocationCode));

                JobPosting posting = JobPosting.builder()
                        .jobId(jobId)
                        .company(company)
                        .title(job.path("position").path("title").asText())
                        .jobUrl(job.path("url").asText())
                        .industry(job.path("position").path("industry").path("name").asText())
                        .jobType(job.path("position").path("job-type").path("name").asText())
                        .location(location)
                        .experienceLevel(job.path("position").path("experience-level").path("code").asInt())
                        .educationLevel(job.path("position").path("required-education-level").path("code").asInt())
                        .salaryCode(job.path("salary").path("code").asInt())
                        .salaryRange(job.path("salary").path("name").asText())
                        .postingDate(LocalDateTime.ofEpochSecond(job.path("posting-timestamp").asLong(), 0, ZoneOffset.UTC))
                        .expirationDate(LocalDateTime.ofEpochSecond(job.path("expiration-timestamp").asLong(), 0, ZoneOffset.UTC))
                        .openingDate(LocalDateTime.ofEpochSecond(job.path("opening-timestamp").asLong(), 0, ZoneOffset.UTC))
                        .closeType(job.path("close-type").path("code").asInt())
                        .viewCount(job.has("read-cnt") ? job.path("read-cnt").asInt() : 0)
                        .applyCount(job.has("apply-cnt") ? job.path("apply-cnt").asInt() : 0)
                        .keyword(job.has("keyword") ? job.path("keyword").asText() : null)
                        .status(JobPosting.Status.ACTIVE)
                        .build();

                jobPostingRepo.save(posting);
//                System.out.println("공고 저장 완료 - 사람인 API 요청 URL: " + apiUrl);
            }
        } catch (Exception e) {
            System.out.println("updateJobPostings 예외 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 금융위원회 기업정보 API를 호출해서 기업 정보를 저장 및 반환하는 함수
    public Company callCompanyInfo(String companyName, String industry) {
        String normalizedName = normalizeCompanyName(companyName);

        Optional<Company> existingCompany = companyRepository.findByName(normalizedName);
        if (existingCompany.isPresent()) {
            return existingCompany.get();
        }

        try {
            String encodedName = URLEncoder.encode(normalizedName, StandardCharsets.UTF_8);
            String financeApiUrl = "https://apis.data.go.kr/1160100/service/GetCorpBasicInfoService_V2/getCorpOutline_V2"
                    + "?serviceKey=" + financeKey
                    + "&pageNo=1&numOfRows=1&resultType=json"
                    + "&corpNm=" + encodedName;

            URL url = new URL(financeApiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            JsonNode root = objectMapper.readTree(response.toString());
            JsonNode items = root.path("response").path("body").path("items").path("item");

            if (items.isArray() && items.size() > 0) {
                JsonNode firstItem = items.get(0);

                String bzno = firstItem.path("bzno").asText();
                bzno = (bzno == null || bzno.isBlank()) ? null : bzno;  // **여기 추가**

                String estbDt = firstItem.path("enpEstbDt").asText();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                LocalDate foundedDate;
                if (estbDt == null || estbDt.isEmpty()) {
                    foundedDate = LocalDate.now();
                } else {
                    foundedDate = LocalDate.parse(estbDt, formatter);
                }

                Company newCompany = Company.builder()
                        .name(firstItem.path("corpNm").asText())
                        .businessNumber(bzno)  // **여기도 수정된 bzno 사용**
                        .representativeName(firstItem.path("enpRprFnm").asText())
                        .industry(industry)
                        .foundedDate(foundedDate)
                        .numEmployees(firstItem.path("enpEmpeCnt").asInt())
                        .revenue(0L)
                        .website(firstItem.path("enpHmpgUrl").asText())
                        .logoKey(null)
                        .address(firstItem.path("enpBsadr").asText())
                        .updatedAt(LocalDateTime.now())
                        .status(Company.Status.ACTIVE)
                        .build();

                Company savedCompany = companyRepository.save(newCompany);
                return savedCompany;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    // 기업명에서 "(주)"나 "㈜" 등의 문자열을 제거하고 공백을 트림하여 정규화합니다.
    private String normalizeCompanyName(String companyName) {
        if (companyName == null) return null;
        return companyName.replaceAll("[\\(（]?\\s*[㈜\\(]?주[\\)）]?\\s*", "").trim();
    }

    public List<JobPosting> getAllJobPostings() {
        return jobPostingRepo.findAll();
    }
}
