package com.highfive.meetu.global.security;

import com.highfive.meetu.domain.auth.personal.type.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomUserPrincipal {
    private Long accountId;
    private Long profileId;  // 개인회원만 존재 (기업/관리자는 null 가능)
    private Role role;       // PERSONAL, BUSINESS, ADMIN
}
