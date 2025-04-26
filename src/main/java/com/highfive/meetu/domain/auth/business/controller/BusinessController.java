package com.highfive.meetu.domain.auth.business.controller;

import com.highfive.meetu.global.common.response.ResultData;
import com.highfive.meetu.global.security.LoginAccountId;
import com.highfive.meetu.global.security.RoleSecured;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 기업회원(Business) 관련 API 컨트롤러
 */
@RestController
@RequestMapping("/api/business")
@RequiredArgsConstructor
public class BusinessController {

    /**
     * 기업회원 전용 대시보드 접근
     */
    @PreAuthorize("hasRole('BUSINESS')")
    @GetMapping("/dashboard")
    public ResultData<String> companyDashboard() {
        return ResultData.success(1, "기업회원 대시보드 진입 성공!");
    }

    /**
     * 기업회원 전용 마이페이지 (accountId 추출 예시)
     */
    @RoleSecured(roles = {"BUSINESS"})
    @GetMapping("/mypage")
    public ResultData<String> businessMypage(@LoginAccountId Long accountId) {
        return ResultData.success(1, "기업회원 마이페이지 (accountId: " + accountId + ")");
    }
}
