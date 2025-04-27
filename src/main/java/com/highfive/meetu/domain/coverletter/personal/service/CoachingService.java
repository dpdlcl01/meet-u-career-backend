package com.highfive.meetu.domain.coverletter.personal.service;

import com.highfive.meetu.domain.coverletter.common.entity.CoverLetterContent;
import com.highfive.meetu.domain.coverletter.common.entity.CoverLetterContentFeedback;
import com.highfive.meetu.domain.coverletter.common.repository.CoverLetterContentFeedbackRepository;
import com.highfive.meetu.domain.coverletter.common.repository.CoverLetterContentRepository;
import com.highfive.meetu.domain.coverletter.common.repository.CoverLetterRepository;
import com.highfive.meetu.domain.coverletter.personal.dto.CoachingRequestDTO;
import com.highfive.meetu.domain.coverletter.personal.dto.CoachingResponseDTO;
import com.highfive.meetu.global.common.exception.BadRequestException;
import com.highfive.meetu.global.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoachingService {

    private final WebClient webClient; // WebClient 의존성 주입
    private final CoverLetterRepository coverLetterRepository;
    private final CoverLetterContentRepository coverLetterContentRepository;
    private final CoverLetterContentFeedbackRepository coverLetterContentFeedbackRepository;

    // application-secret.yml에서 주입받은 FastAPI 엔드포인트 URL
    @Value("${ai.service-url}")
    private String aiServiceUrl;


    /**
     * 메인 메뉴용 가벼운 AI 코칭 요청 (DB 저장 없이)
     */
    public CoachingResponseDTO getSimpleCoaching(String sectionTitle, String content) {
        // 요청 DTO 생성
        CoachingRequestDTO request = CoachingRequestDTO.builder()
                .sectionTitle(sectionTitle)
                .content(content)
                .build();

        // FastAPI 서버로 POST 요청
        return webClient.post()
                .uri(aiServiceUrl + "/coaching")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(CoachingResponseDTO.class)
                .block();
    }


    /**
     * FastAPI로 AI 코칭 요청을 보내고 응답을 받아 db에 저장하는 메서드
     */
    @Transactional
    public CoachingResponseDTO getCoaching(Long contentId, String sectionTitle, String content) {
        // 요청 데이터 생성
        CoachingRequestDTO request = CoachingRequestDTO.builder()
                .contentId(contentId)
                .sectionTitle(sectionTitle)
                .content(content)
                .build();

        // FastAPI 서버로 POST 요청 전송
        CoachingResponseDTO response = webClient.post()
                .uri(aiServiceUrl + "/coaching") // ex: http://ai-service:8000/coaching
                .bodyValue(request) // JSON 본문 전송
                .retrieve()
                .bodyToMono(CoachingResponseDTO.class) // 응답을 DTO로 매핑
                .block(); // 동기식으로 처리 (즉시 결과 반환)

        // 실패했을 경우 기본 메시지 처리
        if (response == null) {
            return new CoachingResponseDTO(contentId, "[AI 서버 응답 실패]", "");
        }

        // contentId에 해당하는 내용 가져오기
        CoverLetterContent contentEntity = coverLetterContentRepository.findById(contentId)
                .orElseThrow(() -> new NotFoundException("자기소개서 내용이 없습니다."));

        // 새 피드백 저장 (이력 관리를 위해 항상 새로 저장)
        CoverLetterContentFeedback feedback = CoverLetterContentFeedback.builder()
                .content(contentEntity)
                .originalContent(content)  // 요청 시점의 원본 내용 저장
                .feedback(response.getFeedback())
                .revisedContent(response.getRevisedContent())
                .isApplied(false)  // 초기에는 미적용 상태
                .build();

        CoverLetterContentFeedback savedFeedback = coverLetterContentFeedbackRepository.save(feedback);

        // 응답에 피드백 ID 추가 (프론트엔드에서 적용 시 사용)
        response.setFeedbackId(savedFeedback.getId());

        return response;
    }

    /**
     * 자기소개서 항목에 AI 피드백 내용을 적용하는 메서드
     */
    @Transactional
    public CoachingResponseDTO applyFeedback(Long contentId, Long feedbackId) {
        // 자기소개서 항목 조회
        CoverLetterContent content = coverLetterContentRepository.findById(contentId)
                .orElseThrow(() -> new NotFoundException("자기소개서 내용이 없습니다."));

        // 피드백 조회
        CoverLetterContentFeedback feedback = coverLetterContentFeedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new NotFoundException("피드백을 찾을 수 없습니다."));

        // 피드백이 해당 항목의 것인지 확인
        if (!feedback.getContent().getId().equals(contentId)) {
            throw new BadRequestException("해당 자기소개서 항목의 피드백이 아닙니다.");
        }

        // 피드백 내용을 자기소개서 항목에 적용
        content.setContent(feedback.getRevisedContent());
        coverLetterContentRepository.save(content);

        // 피드백 상태 업데이트
        feedback.setIsApplied(true);
        feedback.setAppliedAt(LocalDateTime.now());
        coverLetterContentFeedbackRepository.save(feedback);

        // 응답 생성
        return CoachingResponseDTO.builder()
                .contentId(contentId)
                .feedbackId(feedbackId)
                .feedback(feedback.getFeedback())
                .revisedContent(feedback.getRevisedContent())
                .isApplied(true)
                .build();
    }


    /**
     * 피드백이 현재 내용에 적용 가능한지 확인하는 메서드
     */
    public boolean isFeedbackValid(Long contentId, Long feedbackId) {
        CoverLetterContent content = coverLetterContentRepository.findById(contentId)
                .orElseThrow(() -> new NotFoundException("자기소개서 내용이 없습니다."));

        CoverLetterContentFeedback feedback = coverLetterContentFeedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new NotFoundException("피드백을 찾을 수 없습니다."));

        // 피드백 요청 시점의 내용과 현재 내용이 동일한지 확인
        return content.getContent().equals(feedback.getOriginalContent());
    }

}
