package com.highfive.meetu.domain.auth.personal.controller;

import com.highfive.meetu.domain.auth.personal.dto.LoginResponseDTO;
import com.highfive.meetu.domain.auth.personal.service.GoogleLoginService;
import com.highfive.meetu.global.common.response.ResultData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/personal/auth")
@RequiredArgsConstructor
public class GoogleLoginController {
    private final GoogleLoginService googleLoginService;

    @PostMapping("/google/callback")
    public ResultData<LoginResponseDTO> loginWithGoogle(@RequestBody Map<String, String> request) {
        String idToken = request.get("idToken");
        LoginResponseDTO loginResponse = googleLoginService.login(idToken);
        return ResultData.success(1, loginResponse);
    }
}
