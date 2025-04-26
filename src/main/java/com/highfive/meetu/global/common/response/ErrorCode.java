package com.highfive.meetu.global.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 인증 관련
    AUTH_NOT_FOUND("AUTH-001", "로그인 정보가 없습니다."),
    INVALID_TOKEN("AUTH-002", "유효하지 않은 토큰입니다."),

    // 사용자 관련
    USER_NOT_FOUND("USER-001", "사용자를 찾을 수 없습니다."),
    PROFILE_NOT_FOUND("USER-002", "프로필을 찾을 수 없습니다."),

    // 게시글 관련
    POST_NOT_FOUND("POST-001", "게시글을 찾을 수 없습니다."),

    // 서버 내부 에러
    INTERNAL_SERVER_ERROR("SERVER-001", "서버에 오류가 발생했습니다.");

    private final String code;
    private final String message;
}
