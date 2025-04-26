package com.highfive.meetu.domain.job.business.controller;

import com.highfive.meetu.domain.job.business.dto.ApplicantResponseDTO;
import com.highfive.meetu.domain.job.business.service.ApplicantBusinessService;
import com.highfive.meetu.global.common.response.ResultData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/business/applicants")
@RequiredArgsConstructor
public class ApplicantBusinessController {
    private final ApplicantBusinessService applicantBusinessService;

    @GetMapping("/{jobPostingId}")
    public ResultData<List<ApplicantResponseDTO>> getApplicantsByJobPosting(@PathVariable Long jobPostingId) {
        List<ApplicantResponseDTO> applicants = applicantBusinessService.getApplicantsByJobPosting(jobPostingId);
        return ResultData.success(applicants.size(), applicants);
    }
}
