package com.highfive.meetu.domain.job.admin.controller;

import com.highfive.meetu.domain.job.common.entity.JobPosting;
import com.highfive.meetu.domain.job.admin.service.JobPostingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;




@RestController
@RequiredArgsConstructor
@RequestMapping("/api/jobs")
public class JobPostingController {

    private final JobPostingService service;

    // DB에 저장된 채용 공고 조회
    @GetMapping
    public List<JobPosting> getAllJobPostings() {
        return service.getAllJobPostings();
    }

    // 사람인 API를 호출 후 채용공고 업데이트 (DB 저장)
    @PostMapping("/fetch")
    public String updateJobPostings() {
        service.updateJobPostings();  // 변경된 함수 호출
        return "저장 완료!";
    }

    // 금융위 기업정보 API 테스트 (회사명으로 호출)
    @GetMapping("/company")
    public String testFinanceApi(@RequestParam String name, String industry) {
        service.callCompanyInfo(name, industry);  // 변경된 함수 호출
        return "API 호출 완료!";
    }
}
