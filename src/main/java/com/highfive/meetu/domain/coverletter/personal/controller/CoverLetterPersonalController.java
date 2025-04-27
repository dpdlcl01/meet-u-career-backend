package com.highfive.meetu.domain.coverletter.personal.controller;

import com.highfive.meetu.domain.coverletter.personal.dto.*;
import com.highfive.meetu.domain.coverletter.personal.service.CoachingService;
import com.highfive.meetu.domain.coverletter.personal.service.CoverLetterPersonalService;
import com.highfive.meetu.domain.coverletter.personal.service.FitAnalysisService;
import com.highfive.meetu.global.common.response.ResultData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/personal/coverletter")
@RequiredArgsConstructor
public class CoverLetterPersonalController {

    private final CoachingService coachingService;
    private final FitAnalysisService fitAnalysisService;
    private final CoverLetterPersonalService coverLetterPersonalService;

    // 자기소개서 목록
    @GetMapping("/list")
    public ResultData<List<CoverLetterPersonalDTO>> getMyCoverLetters() {
        Long profileId = 1L;
        //Long profileId = SecurityUtil.getProfileId(); // 로그인 사용자 기준

        List<CoverLetterPersonalDTO> result = coverLetterPersonalService.getCoverLetterList(profileId);
        return ResultData.success(result.size(), result);
    }

    // 자기소개서 상세
    @GetMapping("/view")
    public ResultData<CoverLetterPersonalViewDTO> viewCoverLetter(@RequestParam Long id) {
        CoverLetterPersonalViewDTO result = coverLetterPersonalService.getCoverLetterDetail(id);
        return ResultData.success(1, result);
    }


    // 자기소개서 전체 저장
    @PostMapping("/create")
    public ResultData<String> createCoverLetter(@RequestBody CoverLetterPersonalDTO dto) {
        coverLetterPersonalService.saveCoverLetter(1L, dto);
        return ResultData.success(1, "자기소개서 전체 저장");
    }

    // 자기소개서 수정
    @PostMapping("/edit/{coverLetterId}")
    public ResultData<String> updateCoverLetter(
            @PathVariable Long coverLetterId,
            @RequestBody CoverLetterPersonalDTO dto
    ) {
        Long profileId = 1L;
        // Long profileId = SecurityUtil.getProfileId(); // 로그인 사용자 기준

        coverLetterPersonalService.updateCoverLetter(coverLetterId, dto);
        return ResultData.success(1, "자기소개서 수정 완료");
    }


    // 자기소개서 삭제
    @PostMapping("/{id}")
    public ResultData<Void> deleteCoverLetter(@PathVariable Long id) {
        coverLetterPersonalService.deleteCoverLetter(id);
        return ResultData.success(0, null);
    }


    // 1. 전체 공용 (비회원/회원) - 저장 없이 코칭만
    @PostMapping("/coaching")
    public ResultData<CoachingResponseDTO> getCoaching(@RequestBody CoachingRequestDTO dto) {
        CoachingResponseDTO response = coachingService.getSimpleCoaching(
                dto.getSectionTitle(),
                dto.getContent()
        );
        return ResultData.success(1, response);
    }

    // 2. 회원 전용 (자기소개서 저장용)
    @PostMapping("/{contentId}/save-coaching")
    public ResultData<CoachingResponseDTO> saveCoaching(
            @PathVariable Long contentId,
            @RequestBody CoachingRequestDTO dto
    ) {
        CoachingResponseDTO response = coachingService.getCoaching(
                contentId,
                dto.getSectionTitle(),
                dto.getContent()
        );
        return ResultData.success(1, response);
    }

    // 피드백 적용
    @PostMapping("/{contentId}/apply-feedback/{feedbackId}")
    public ResultData<CoachingResponseDTO> applyFeedback(
            @PathVariable Long contentId,
            @PathVariable Long feedbackId
    ) {
        CoachingResponseDTO response = coachingService.applyFeedback(contentId, feedbackId);
        return ResultData.success(1, response);
    }


    // 직무 적합도 분석
    @PostMapping("/fit-analysis")
    public ResultData<FitAnalysisResponseDTO> analyzeFit(@RequestBody FitAnalysisRequestDTO dto) {
        FitAnalysisResponseDTO result = fitAnalysisService.analyzeAndSave(dto);
        return ResultData.success(1, result);
    }


}
