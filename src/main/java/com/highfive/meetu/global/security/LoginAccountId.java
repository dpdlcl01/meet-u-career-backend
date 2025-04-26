package com.highfive.meetu.global.security;

import java.lang.annotation.*;

/**
 * 현재 로그인한 사용자의 accountId를 주입받는 어노테이션
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LoginAccountId {
}
