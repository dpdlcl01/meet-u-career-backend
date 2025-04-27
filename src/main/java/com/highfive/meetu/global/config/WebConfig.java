package com.highfive.meetu.global.config;

import com.highfive.meetu.global.security.LoginAccountIdArgumentResolver;
import com.highfive.meetu.global.security.LoginUserArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * WebMvcConfigurer 설정
 * - 커스텀 ArgumentResolver 등록
 * - CORS 정책 설정
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

  private final LoginAccountIdArgumentResolver loginAccountIdArgumentResolver;
  private final LoginUserArgumentResolver loginUserArgumentResolver;

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(loginAccountIdArgumentResolver);
    resolvers.add(loginUserArgumentResolver);
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**") // 모든 경로에 대해 CORS 허용
        .allowedOrigins(
            "http://localhost:3000",
            "https://meet-u-career.com",
            "https://api.meet-u-career.com"
        )
        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        .allowedHeaders("*")
        .allowCredentials(true); // 인증정보(쿠키 등) 포함 허용
  }
}
