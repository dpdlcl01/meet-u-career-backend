package com.highfive.meetu.domain.auth.personal.controller;

import com.highfive.meetu.domain.auth.personal.dto.LoginResponseDTO;
import com.highfive.meetu.domain.auth.personal.service.KakaoLoginService;
import com.highfive.meetu.domain.user.common.entity.Account;
import com.highfive.meetu.global.common.response.ResultData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/personal/auth")
public class KakaoLoginController {
    private final KakaoLoginService kakaoLoginService;

    @GetMapping("/kakao/callback")
    public ResultData<LoginResponseDTO> loginWithKakao(@RequestParam("code") String code) {
        LoginResponseDTO loginResponse = kakaoLoginService.kakaoLoginOrSignup(code);
        return ResultData.success(1, loginResponse);
    }
}
