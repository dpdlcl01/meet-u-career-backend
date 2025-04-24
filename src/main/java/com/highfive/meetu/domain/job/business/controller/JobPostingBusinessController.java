package com.highfive.meetu.domain.job.business.controller;

import com.highfive.meetu.domain.job.business.dto.JobPostingResponseDTO;
import com.highfive.meetu.domain.job.business.service.JobPostingBusinessService;
import com.highfive.meetu.global.common.response.ResultData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/business/jobpostings")
@RequiredArgsConstructor
public class JobPostingBusinessController {
    private final JobPostingBusinessService jobPostingBusinessService;

    @GetMapping
    public ResultData<List<JobPostingResponseDTO>> getMyCompanyJobPostings() {
        List<JobPostingResponseDTO> postings = jobPostingBusinessService.getMyCompanyJobPostings();
        return ResultData.success(postings.size(), postings);
    }
}
