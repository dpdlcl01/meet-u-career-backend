package com.highfive.meetu.domain.auth.admin.controller;

import com.highfive.meetu.global.common.response.ResultData;
import com.highfive.meetu.global.security.LoginAccountId;
import com.highfive.meetu.global.security.RoleSecured;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 관리자(Admin) 관련 API 컨트롤러
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    /**
     * 일반 관리자, 슈퍼 관리자 모두 접근 가능한 대시보드
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER')")
    @GetMapping("/dashboard")
    public ResultData<String> adminDashboard() {
        return ResultData.success(1, "관리자 대시보드 진입 성공!");
    }

    /**
     * RoleSecured: ADMIN 권한만 접근 가능 (accountId 추출 예시용)
     */
    @RoleSecured(roles = {"ADMIN"})
    @GetMapping("/mypage")
    public ResultData<String> adminDashboard(@LoginAccountId Long accountId) {
        return ResultData.success(1, "관리자 마이페이지 (accountId: " + accountId + ")");
    }
}
