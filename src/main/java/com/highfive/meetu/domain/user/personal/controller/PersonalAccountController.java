package com.highfive.meetu.domain.user.personal.controller;

import com.highfive.meetu.domain.user.personal.dto.CertificationRequestDTO;
import com.highfive.meetu.domain.user.personal.dto.CertificationVerifyRequestDTO;
import com.highfive.meetu.domain.user.personal.dto.PersonalEmailRequestDTO;
import com.highfive.meetu.domain.user.personal.dto.PersonalSignUpRequestDTO;
import com.highfive.meetu.domain.user.personal.service.CertificationService;
import com.highfive.meetu.domain.user.personal.service.PersonalAccountService;
import com.highfive.meetu.domain.user.personal.type.CertificationVerifyResult;
import com.highfive.meetu.global.common.response.ResultData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/personal/account")
public class PersonalAccountController {
    private final PersonalAccountService accountService;
    private final CertificationService certificationService;

    // 이메일(아이디) 중복 체크
    @PostMapping("/check/userid")
    public ResultData<Boolean> findByUserId(String userId) {
        boolean result = accountService.findByUserId(userId);

        if (result) {
            return ResultData.of(1, "이미 존재하는 아이디입니다.", null);
        } else {
            return ResultData.of(0, "사용 가능한 아이디입니다.", null);
        }
    }

    // 이메일(아이디) 중복 체크
    @PostMapping("/check/email")
    public ResultData<Boolean> findByEmail(@RequestBody PersonalEmailRequestDTO dto) {
        boolean result = accountService.findByEmail(dto.getEmail());

        if (result) {
            return ResultData.of(1, "이미 존재하는 이메일입니다.", null);
        } else {
            return ResultData.of(0, "사용 가능한 이메일입니다.", null);
        }
    }

    // 이메일 인증 요청
    @PostMapping("/certification")
    public ResultData<?> sendCertificationEmail(@RequestBody CertificationRequestDTO request) {
        certificationService.sendCertification(request.getName(), request.getEmail());
        return ResultData.success(1, "인증 코드를 발송했습니다.");
    }

    // 이메일 인증 검증
    @PostMapping("/certification/verify")
    public ResultData<?> verifyCertification(@RequestBody CertificationVerifyRequestDTO request) {
        CertificationVerifyResult result = certificationService.verifyCertification(
            request.getEmail(),
            request.getCode()
        );

        if (result == CertificationVerifyResult.SUCCESS) {
            return ResultData.success(1, "인증 성공");
        } else if (result == CertificationVerifyResult.EXPIRED) {
            return ResultData.fail("인증 시간이 만료되었습니다. 다시 요청해 주세요.");
        } else {
            return ResultData.fail("인증 코드가 일치하지 않습니다.");
        }
    }


    // 개인회원 회원가입
    @PostMapping("/signup")
    public ResultData<PersonalSignUpRequestDTO> signupPersonal(@RequestBody PersonalSignUpRequestDTO dto) {
        PersonalSignUpRequestDTO result = accountService.save(dto);

        if (result != null) {
            return ResultData.success(1, result);
        } else {
            return ResultData.fail();
        }
    }
}
