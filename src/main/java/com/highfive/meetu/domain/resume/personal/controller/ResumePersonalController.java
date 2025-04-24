package com.highfive.meetu.domain.resume.personal.controller;

import com.highfive.meetu.domain.application.personal.service.ApplicationPersonalService;
import com.highfive.meetu.domain.resume.personal.dto.ResumePersonalDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.highfive.meetu.domain.resume.personal.dto.*;
import com.highfive.meetu.domain.resume.personal.service.ResumePersonalService;
import com.highfive.meetu.global.common.exception.BadRequestException;
import com.highfive.meetu.global.common.response.ResultData;
import com.highfive.meetu.infra.oauth.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/personal/resume")
@RequiredArgsConstructor
public class ResumePersonalController {

    private final ResumePersonalService resumePersonalService;
    private final ApplicationPersonalService applicationPersonalService;
    private final ObjectMapper objectMapper;

//    // 이력서 초기 생성 메서드 - "이력서 작성" 버튼을 눌러서 이력서 작성 페이지로 넘어갈 때 데이터 생성
//    @PostMapping("/init")
//    public ResultData<Long> initResume(@RequestBody Map<String, Object> request) {
//        // 필수 파라미터 검사
//        if (request.get("profileId") == null) {
//            throw new BadRequestException("필수 입력값(profileId)이 누락되었습니다.");
//        }
//        if (request.get("resumeType") == null) {
//            throw new BadRequestException("필수 입력값(resumeType)이 누락되었습니다.");
//        }
//
//        Long profileId = Long.valueOf(request.get("profileId").toString());
//        Integer resumeType = Integer.valueOf(request.get("resumeType").toString());
//
//        Long resumeId = resumePersonalService.initResume(profileId, resumeType);
//        return ResultData.success(1, resumeId);
//    }
//
//    // 이력서 유형: 파일 (resumeType = 1) 한 번에 생성
//    // POST /api/personal/resume/file
//    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResultData<Long> saveFileResume(
//            @RequestPart("data") String data,
//            @RequestPart("file") MultipartFile file
//    ) throws JsonProcessingException {
//        // JSON 문자열 → DTO 수동 파싱
//        ResumeFileDTO dto = objectMapper.readValue(data, ResumeFileDTO.class);
//        Long resumeId = resumePersonalService.saveFileResume(dto, file);
//        return ResultData.success(1, resumeId);
//    }


//    // 기존 파일 이력서 수정 또는 init 이후 호출시
//    @PostMapping("/{resumeId}/file")
//    public ResultData<String> updateFileResume(
//            @PathVariable Long resumeId,
//            @RequestPart("file") MultipartFile resumeFile
//    ) {
//        resumePersonalService.updateFileResume(resumeId, resumeFile);
//        return ResultData.success(1, "파일 이력서가 저장되었습니다.");
//    }



//    // 이력서 유형: URL (resumeType = 2)
//    // POST /api/personal/resume/url
//    @PostMapping("/url")
//    public ResultData<Long> saveUrlResume(
//            @RequestBody ResumeUrlDTO dto
//    ) {
//        Long resumeId = resumePersonalService.saveUrlResume(dto);
//        return ResultData.success(1, resumeId);
//    }




//    // 이력서 기본 정보 저장
//    @PostMapping("/{resumeId}/info")
//    public ResultData<String> updateResumeBasicInfo(
//            @PathVariable Long resumeId,
//            @RequestPart("data") String data, // String으로 받기
//            @RequestPart(value = "resumeFile", required = false) MultipartFile resumeFile,
//            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
//    ) throws JsonProcessingException {
//
//        // 수동 파싱
//        ResumeBasicInfoDTO dto = objectMapper.readValue(data, ResumeBasicInfoDTO.class);
//
//        // 디버깅 출력
//        System.out.println("title = " + dto.getTitle());
//        System.out.println("status = " + dto.getStatus());
//
//
//        resumePersonalService.updateResumeBasicInfo(resumeId, dto, resumeFile, profileImage);
//        return ResultData.success(1, "이력서 기본 정보가 저장되었습니다.");
//    }
//
//
//    // 항목 추가 메서드
//    @PostMapping("/{resumeId}/content")
//    public ResultData<Long> addResumeContent(
//            @PathVariable Long resumeId,
//            @RequestPart("data") String data,
//            @RequestPart(value = "file", required = false) MultipartFile file
//    ) throws JsonProcessingException {
//
//        ResumeContentDTO dto = objectMapper.readValue(data, ResumeContentDTO.class);
//        dto.setResumeId(resumeId);
//
//        dto.setResumeId(resumeId);
//        Long contentId = resumePersonalService.saveResumeContent(dto, file);
//
//        return ResultData.success(1, contentId);
//    }
//
//
//    // 항목 수정 메서드
//    @PostMapping("/{resumeId}/content/{contentId}")
//    public ResultData<Long> updateResumeContent(
//            @PathVariable Long resumeId,
//            @PathVariable Long contentId,
//            @RequestPart("data") String data,
//            @RequestPart(value = "file", required = false) MultipartFile file
//    ) throws JsonProcessingException {
//
//        ResumeContentDTO dto = objectMapper.readValue(data, ResumeContentDTO.class);
//        dto.setResumeId(resumeId);
//        dto.setId(contentId);
//
//        Long updatedId = resumePersonalService.updateResumeContent(dto, file);
//        return ResultData.success(1, updatedId);
//    }
//
//
//
//    // 항목 삭제 메서드
//    @DeleteMapping("/{resumeId}/content/{contentId}")
//    public ResultData<String> deleteResumeContent(
//            @PathVariable Long resumeId,
//            @PathVariable Long contentId) {
//
//        resumePersonalService.deleteResumeContent(resumeId, contentId);
//        return ResultData.success(1, "이력서 항목이 삭제되었습니다.");
//    }


    // 최종 "이력서 저장하기" 버튼 클릭시 전체 작성 내용이 저장되는 메서드
    /**
     * 이력서 전체 저장 (JSON + 파일 포함)
     */
    @PostMapping("/saveall")
    public ResultData<String> finalSaveResume(
            @RequestPart("data") String data, // 프론트는 문자열로 보내므로 이렇게 받아야 함
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestPart(value = "resumeFile", required = false) MultipartFile resumeFile,
            @RequestPart(value = "contentFiles", required = false) List<MultipartFile> contentFiles
    ) throws JsonProcessingException {

        // 문자열 → DTO 수동 파싱
        ResumeWriteRequestDTO dto = objectMapper.readValue(data, ResumeWriteRequestDTO.class);

        // 파일 주입
        dto.setProfileImage(profileImage);
        dto.setResumeFile(resumeFile);
        dto.setContentFiles(contentFiles);

        // 실제 저장
        resumePersonalService.saveAllAtOnce(dto);

        return ResultData.success(1, "이력서 전체 저장 완료");
    }






    /**
     * 내 이력서 목록 조회
     *
     * @param //profileId 로그인한 사용자의 프로필 ID
     * @return ResultData (count, data)
     */
    @GetMapping("/list")
    public ResultData<List<ResumePersonalDTO>> getMyResumeList() {

        Long profileId = 1L;

        // 서비스 단에서 profileId를 기반으로 해당 사용자의 이력서 리스트를 조회
        List<ResumePersonalDTO> resumeList = resumePersonalService.getResumeListByProfileId(profileId);

        // ResultData 포맷으로 응답 반환 (count: 리스트 크기, data: 리스트)
        return ResultData.success(resumeList.size(), resumeList);
    }


    /**
     * 이력서 상세 조회
     *
     * @param resumeId 조회할 이력서 ID
     * @return 이력서 상세 정보
     */
    @GetMapping("/view/{resumeId}")
    public ResultData<ResumePersonalDTO> getResumeDetail(@PathVariable Long resumeId) {
        ResumePersonalDTO resumeDetail = resumePersonalService.getResumeDetail(resumeId);
        return ResultData.success(1, resumeDetail);
    }

//    /**
//     * 이력서 작성 (임시저장 또는 최초 생성)
//     *
//     * @param dto 작성할 이력서 정보 (제목, 개요 등 최소 정보 포함)
//     * @return ResultData(1, 생성된 resumeId)
//     */
//    @PostMapping("/create")
//    public ResultData<Long> createResume(@RequestBody ResumePersonalDTO dto) {
//        Long resumeId = resumePersonalService.createResume(dto);
//        return ResultData.success(1, resumeId); // ID만 응답
//    }
//
//
//    /**
//     * 이력서 전체 수정
//     * - 이력서 기본 정보(제목, 개요 등) + 항목 리스트(ResumeContent) 모두 수정
//     *
//     * @param resumeId 수정 대상 이력서 ID
//     * @param dto 수정할 내용이 포함된 ResumePersonalDTO
//     * @return 수정된 이력서 ID
//     */
//    @PutMapping("/edit/{resumeId}")
//    public ResultData<Long> updateResumeAll(@PathVariable Long resumeId, @RequestBody ResumePersonalDTO dto) {
//
//        resumePersonalService.updateResumeAll(resumeId, dto);
//
//        return ResultData.success(1, resumeId);
//    }



    // -------------------------------

    // 대표 이력서 설정 (isPrimary = true)
    @PatchMapping("/{resumeId}/represent")
    public ResultData<String> setPrimaryResume(@PathVariable Long resumeId) {
        resumePersonalService.setAsPrimaryResume(resumeId);
        return ResultData.success(1, "대표 이력서로 설정되었습니다.");
    }

    /**
     * 이력서 목록 조회
     */
//    @GetMapping("/list")
//    public ResultData<List<ResumePersonalDTO>> getMyResumeList() {
//        Long profileId = SecurityUtil.getProfileId();
//        List<ResumePersonalDTO> resumeList = resumePersonalService.getResumeListByProfileId(profileId);
//        return ResultData.success(resumeList.size(), resumeList);
//    }

    /**
     * 입사 지원
     * @param jobPostingId 채용 공고 ID
     * @param resumeId 이력서 ID
     * @return 결과 메시지
     */
    @PostMapping("/apply")
    public ResultData<?> applyForJob(@RequestParam Long jobPostingId, @RequestParam Long resumeId) {
        Long profileId = SecurityUtil.getProfileId();  // 로그인한 사용자의 프로필 ID 가져오기
        applicationPersonalService.applyForJob(profileId, jobPostingId, resumeId);
        return ResultData.success(1, "입사 지원이 완료되었습니다.");
    }
    // 이력서 파일 다운로드 Presigned URL 발급
    @GetMapping("/{resumeId}/file-url")
    public ResultData<String> getResumeFileDownloadUrl(@PathVariable Long resumeId) {
        String url = resumePersonalService.generateResumeFileDownloadUrl(resumeId);
        return ResultData.success(1, url);
    }

    // 이력서 삭제 (Soft Delete 방식)
    @DeleteMapping("/{resumeId}")
    public ResultData<?> deleteResume(@PathVariable Long resumeId) {
        resumePersonalService.softDeleteResume(resumeId);
        return ResultData.success(1, null);
    }



//    // 이력서 상태 변경
//    @PatchMapping("/{resumeId}/status")
//    public ResultData<String> updateStatus(@PathVariable Long resumeId, @RequestBody Map<String, Integer> body) {
//        // 상태 값 존재 확인
//        if (body.get("status") == null) {
//            throw new BadRequestException("필수 입력값(status)이 누락되었습니다.");
//        }
//
//        Integer status = body.get("status");
//        resumePersonalService.updateResumeStatus(resumeId, status);
//        return ResultData.success(1, "상태가 변경되었습니다.");
//    }


//    // 이력서 조회수 조회
//    @GetMapping("/{resumeId}/view-count")
//    public ResultData<Integer> getViewCount(@PathVariable Long resumeId) {
//        int count = resumePersonalService.getViewCount(resumeId);
//        return ResultData.success(1, count);
//    }



}