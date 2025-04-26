package com.highfive.meetu.domain.company.personal.controller;

import com.highfive.meetu.domain.company.personal.dto.CompanyFollowRequest;
import com.highfive.meetu.domain.company.personal.service.CompanyFollowService;
import com.highfive.meetu.global.common.response.ResultData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 관심 기업 등록/해제 API 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/personal/company")
public class CompanyFollowController {

    private final CompanyFollowService companyFollowService;

    /**
     * 관심 기업 등록
     *
     * @param request 등록할 기업 ID를 담은 요청 객체
     * @return 등록 성공 메시지
     */
    @PostMapping("/follow")
    public ResultData<?> followCompany(@RequestBody CompanyFollowRequest request) {
        companyFollowService.follow(request.getCompanyId());
        return ResultData.success(1, "관심 기업 등록 완료");
    }

    /**
     * 관심 기업 해제
     *
     * @param request 해제할 기업 ID를 담은 요청 객체
     * @return 해제 성공 메시지
     */
    @PostMapping("/unfollow")
    public ResultData<?> unfollowCompany(@RequestBody CompanyFollowRequest request) {
        companyFollowService.unfollow(request.getCompanyId());
        return ResultData.success(1, "관심 기업 해제 완료");
    }
}
