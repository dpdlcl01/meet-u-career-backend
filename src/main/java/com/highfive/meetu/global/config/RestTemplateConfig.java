// src/main/java/com/highfive/meetu/global/config/RestTemplateConfig.java
package com.highfive.meetu.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import java.util.ArrayList;
import java.util.List;

/**
 * 전역 설정: RestTemplate 빈 등록
 * 외부 API 호출 시 RestTemplate을 주입받아 사용합니다.
 */
@Configuration
public class RestTemplateConfig {

  /**
   * RestTemplate 빈 등록
   * DART API 등 외부 HTTP 호출에 사용됩니다.
   *
   * @return RestTemplate 인스턴스
   */
  @Bean
  public RestTemplate restTemplate() {
    RestTemplate restTemplate = new RestTemplate();
    // Enhance default Jackson converter to read JSON even if content-type is text/xml
    List<HttpMessageConverter<?>> converters = restTemplate.getMessageConverters();
    for (HttpMessageConverter<?> converter : converters) {
      if (converter instanceof MappingJackson2HttpMessageConverter) {
        MappingJackson2HttpMessageConverter mc = (MappingJackson2HttpMessageConverter) converter;
        List<MediaType> types = new ArrayList<>(mc.getSupportedMediaTypes());
        types.add(MediaType.TEXT_XML);
        types.add(MediaType.APPLICATION_XML);
        mc.setSupportedMediaTypes(types);
        break;
      }
    }
    return restTemplate;
  }
}