package com.highfive.meetu.domain.user.common.controller;

import com.highfive.meetu.domain.auth.personal.type.Role;
import com.highfive.meetu.domain.user.common.entity.Account;
import com.highfive.meetu.domain.user.common.entity.Admin;
import com.highfive.meetu.domain.user.common.entity.Profile;
import com.highfive.meetu.domain.user.common.repository.AccountRepository;
import com.highfive.meetu.domain.user.common.repository.AdminRepository;
import com.highfive.meetu.domain.user.common.repository.ProfileRepository;
import com.highfive.meetu.domain.user.common.service.AccountService;
import com.highfive.meetu.domain.user.personal.dto.PersonalInfoDTO;
import com.highfive.meetu.global.common.response.ResultData;
import com.highfive.meetu.global.security.CustomUserPrincipal;
import com.highfive.meetu.global.security.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.highfive.meetu.global.common.response.ErrorCode.*;

/**
 * 공통 로그인 사용자 정보 API
 * - 개인/기업/관리자 공용
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    /**
     * 내 정보 조회 API (공통)
     */
    @GetMapping("/me")
    public ResultData<PersonalInfoDTO> getMyInfo(@LoginUser CustomUserPrincipal user) {
        return ResultData.success(1, accountService.getMyInfo(user));
    }
}
