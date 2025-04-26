package com.highfive.meetu.global.security;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE}) // 메서드, 클래스에 붙일 수 있음
@Retention(RetentionPolicy.RUNTIME)             // 런타임까지 유지
@Documented
public @interface RoleSecured {
    String[] roles(); // 접근 가능한 Role 배열
}
