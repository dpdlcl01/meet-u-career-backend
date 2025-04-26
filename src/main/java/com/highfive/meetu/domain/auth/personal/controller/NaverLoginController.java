package com.highfive.meetu.domain.auth.personal.controller;

import com.highfive.meetu.domain.auth.personal.dto.LoginResponseDTO;
import com.highfive.meetu.domain.auth.personal.service.NaverLoginService;
import com.highfive.meetu.global.common.response.ResultData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/personal/auth")
public class NaverLoginController {
    private final NaverLoginService naverLoginService;

    @GetMapping("/naver/callback")
    public ResultData<LoginResponseDTO> loginWithNaver(@RequestParam("code") String code, @RequestParam("state") String state) {
        LoginResponseDTO loginResponse = naverLoginService.naverLoginOrSignup(code, state);
        return ResultData.success(1, loginResponse);
    }
}
