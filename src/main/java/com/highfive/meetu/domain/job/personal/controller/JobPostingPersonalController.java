package com.highfive.meetu.domain.job.personal.controller;

import com.highfive.meetu.domain.job.personal.dto.JobPostingDetailDTO;
import com.highfive.meetu.domain.job.personal.service.JobPostingPersonalService;
import com.highfive.meetu.global.common.response.ResultData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/jobpostings")
public class JobPostingPersonalController {
    private final JobPostingPersonalService jobPostingPersonalService;

    /**
     * 공고 상세 조회
     */
    @GetMapping("/{jobpostingId}")
    public ResultData<JobPostingDetailDTO> getJobPostingDetail(@PathVariable Long jobpostingId) {
        return jobPostingPersonalService.getJobPostingDetail(jobpostingId);
    }
}
