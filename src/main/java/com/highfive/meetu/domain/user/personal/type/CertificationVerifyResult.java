package com.highfive.meetu.domain.user.personal.type;

public enum CertificationVerifyResult {
    SUCCESS,  // 인증 성공
    EXPIRED,  // 인증 시간 만료
    FAIL      // 인증 코드 불일치
}