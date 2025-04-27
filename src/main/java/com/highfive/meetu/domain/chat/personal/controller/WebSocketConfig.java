package com.highfive.meetu.domain.chat.personal.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket 설정
 * - STOMP를 이용한 WebSocket 메시징 구성
 * - 클라이언트와 서버 간의 pub/sub 통신 구조 정의
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final StompHandler stompHandler;

  /**
   * WebSocket 연결 Endpoint 설정
   * 클라이언트가 연결할 URL: ws://서버주소/ws-stomp
   */
  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws-stomp")
        .setAllowedOriginPatterns("*")  // 모든 오리진 허용 (개발 환경에서는 OK, 배포 시에는 명시적으로 지정하는 것이 좋음)
        .withSockJS();                   // SockJS 지원 (WebSocket 미지원 브라우저 대응)
  }

  /**
   * STOMP 메시징 경로 설정
   * /pub/** : 클라이언트 → 서버 메시지 전송 경로
   * /sub/** : 서버 → 클라이언트 메시지 구독 경로
   */
  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.enableSimpleBroker("/sub"); // 구독용 Prefix
    registry.setApplicationDestinationPrefixes("/pub"); // 발행용 Prefix
  }

  /**
   * WebSocket 연결 인증 및 세션 관리를 위한 핸들러 등록
   */
  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(stompHandler); // 커스텀 핸들러 연결
  }
}
