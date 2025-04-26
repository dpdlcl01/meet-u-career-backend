package com.highfive.meetu.global.security;

import com.highfive.meetu.domain.auth.personal.type.Role;
import com.highfive.meetu.global.common.exception.ForbiddenException;
import com.highfive.meetu.infra.oauth.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RoleSecuredAspect {

    private final SecurityUtil securityUtil;

    @Before("@annotation(roleSecured)")
    public void checkRole(JoinPoint joinPoint, RoleSecured roleSecured) {
        // 현재 인증된 사용자 정보 가져오기
        Role userRole = securityUtil.getUserRole(); // static 메서드 사용

        // 허용된 roles
        String[] allowedRoles = roleSecured.roles();

        // 사용자 Role이 허용된 Role 안에 포함되어 있는지 확인
        boolean hasPermission = Arrays.stream(allowedRoles)
                .anyMatch(role -> role.equals(userRole.name()));

        if (!hasPermission) {
            throw new ForbiddenException("접근 권한이 없습니다.");
        }
    }
}