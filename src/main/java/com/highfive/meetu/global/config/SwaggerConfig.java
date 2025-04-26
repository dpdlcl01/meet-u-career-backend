package com.highfive.meetu.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger (OpenAPI) 설정
 * - Swagger UI 경로: http://localhost:8080/swagger-ui.html
 * - JWT 인증 헤더 자동 적용
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "MeetU API",
                version = "v1",
                description = """
            MeetU 커리어 매칭 플랫폼 API 문서

            개인회원:
            - 이력서, 자기소개서, 공고 조회 등 기능 사용 가능

            기업회원:
            - 공고 등록, 지원자 열람, 제안 기능 사용 가능

            관리자:
            - 사용자 관리, 공고 검수, 로그 관리 기능 사용 가능

            JWT 토큰을 통해 인증 후 API를 테스트할 수 있습니다.
            """
        ),
        security = @SecurityRequirement(name = "bearerAuth")  // 모든 API에 인증 적용
)
@SecurityScheme(
        name = "bearerAuth",                         // 인증 스키마 이름
        type = SecuritySchemeType.HTTP,              // HTTP 기반 인증
        scheme = "bearer",                           // Bearer 토큰 사용
        bearerFormat = "JWT",                        // JWT 포맷 명시
        in = SecuritySchemeIn.HEADER                 // Authorization 헤더에서 받음
)
public class SwaggerConfig {
}

