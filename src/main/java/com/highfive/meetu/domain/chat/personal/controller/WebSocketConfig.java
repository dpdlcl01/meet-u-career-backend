package com.highfive.meetu.domain.chat.personal.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final StompHandler stompHandler;

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    // 클라이언트가 WebSocket 연결을 맺을 주소
    registry.addEndpoint("/ws-stomp").setAllowedOriginPatterns("*");
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    // 구독 주소(prefix)
    registry.enableSimpleBroker("/sub");
    // 발행 주소(prefix)
    registry.setApplicationDestinationPrefixes("/pub");
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    // 연결 이벤트 감지 인터셉터 등록
    registration.interceptors(stompHandler);
  }
}
