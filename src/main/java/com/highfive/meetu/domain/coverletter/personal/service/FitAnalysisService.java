package com.highfive.meetu.domain.coverletter.personal.service;

import com.highfive.meetu.domain.coverletter.common.entity.CoverLetterContent;
import com.highfive.meetu.domain.coverletter.common.entity.CoverLetterContentFitAnalysis;
import com.highfive.meetu.domain.coverletter.common.repository.CoverLetterContentFitAnalysisRepository;
import com.highfive.meetu.domain.coverletter.common.repository.CoverLetterContentRepository;
import com.highfive.meetu.domain.coverletter.personal.dto.FitAnalysisRequestDTO;
import com.highfive.meetu.domain.coverletter.personal.dto.FitAnalysisResponseDTO;
import com.highfive.meetu.domain.job.common.entity.JobCategory;
import com.highfive.meetu.domain.job.common.repository.JobCategoryRepository;
import com.highfive.meetu.global.common.exception.BadRequestException;
import com.highfive.meetu.global.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FitAnalysisService {

    private final WebClient webClient;
    private final CoverLetterContentRepository coverLetterContentRepository;
    private final CoverLetterContentFitAnalysisRepository coverLetterContentFitAnalysisRepository;
    private final JobCategoryRepository jobCategoryRepository;

    @Value("${ai.service-url}")
    private String aiServiceUrl;

    /**
     * 자기소개서 항목에 대한 직무 적합도 분석을 수행하고 결과를 저장하는 메서드
     */
    @Transactional
    public FitAnalysisResponseDTO analyzeAndSave(FitAnalysisRequestDTO dto) {

        // 1. FastAPI 서버로 분석 요청
        FitAnalysisResponseDTO response = webClient.post()
                .uri(aiServiceUrl + "/fit-analysis")
                .bodyValue(Map.of(
                        "contentId", dto.getContentId(),
                        "content", dto.getContent(),
                        "jobTitle", dto.getJobTitle(),
                        "jobPostingText", dto.getJobPostingText()
                ))
                .retrieve()
                .bodyToMono(FitAnalysisResponseDTO.class)
                .block();   // 동기식 호출

        if (response == null) {
            throw new BadRequestException("AI 적합도 분석 결과가 없습니다.");
        }

        // 2. 엔티티 조회
        CoverLetterContent contentEntity = coverLetterContentRepository.findById(dto.getContentId())
                .orElseThrow(() -> new NotFoundException("자기소개서 항목을 찾을 수 없습니다."));

        JobCategory jobCategoryEntity = jobCategoryRepository.findById(dto.getJobCategoryId())
                .orElseThrow(() -> new NotFoundException("직무 카테고리를 찾을 수 없습니다."));

        // 3. 결과 저장
        CoverLetterContentFitAnalysis analysis = CoverLetterContentFitAnalysis.builder()
                .content(contentEntity)
                .jobCategory(jobCategoryEntity)
                .fitScore(response.getFitScore())
                .comment(response.getComment())
                .build();

        coverLetterContentFitAnalysisRepository.save(analysis);

        // 4. 결과 반환
        return response;
    }
}
