package com.highfive.meetu.domain.user.business.controller;

import com.highfive.meetu.domain.user.business.dto.BusinessEmailRequestDTO;
import com.highfive.meetu.domain.user.business.dto.BusinessSignUpRequestDTO;
import com.highfive.meetu.domain.user.business.service.BusinessAccountService;
import com.highfive.meetu.domain.user.personal.dto.PersonalSignUpRequestDTO;
import com.highfive.meetu.global.common.response.ResultData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account/business")
public class BusinessAccountController {
    private final BusinessAccountService accountService;

    // 이메일(아이디) 중복 체크
    @PostMapping("/check/email")
    public ResultData<Boolean> findByEmail(@RequestBody BusinessEmailRequestDTO dto) {
        boolean result = accountService.findByEmail(dto.getEmail());
        return ResultData.success(result ? 1 : 0, result);
    }

    // 기업회원 회원가입
    @PostMapping("/signup")
    public ResultData<BusinessSignUpRequestDTO> signupBusiness(@RequestBody BusinessSignUpRequestDTO dto) {
        BusinessSignUpRequestDTO result = accountService.save(dto);
        return ResultData.success((result != null) ? 1 : 0, result);
    }
}
