package com.highfive.meetu.domain.chat.personal.controller;

import com.highfive.meetu.domain.chat.common.entity.ChatMessage;
import com.highfive.meetu.domain.chat.personal.dto.ChatMessageDTO;
import com.highfive.meetu.domain.chat.personal.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@RestController
@RequiredArgsConstructor
public class SocketController {

  private static final Logger LOGGER = LoggerFactory.getLogger(SocketController.class);

  private final SimpMessageSendingOperations messagingTemplate;
  private final ChatMessageService chatMessageService;

  @EventListener
  public void handleWebSocketConnectListener(SessionConnectEvent event) {
    LOGGER.info("📡 WebSocket 연결 감지됨");
  }

  @EventListener
  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
    LOGGER.info("📴 연결 종료됨 sessionId={}", accessor.getSessionId());
  }

  @MessageMapping("/chat/send")
  public void sendMessage(ChatMessageDTO dto) {
    // 1. DB 저장
    ChatMessage savedMessage = chatMessageService.saveMessage(dto);

    // 2. 구독자에게 브로드캐스트
    messagingTemplate.convertAndSend("/sub/chat/" + dto.getRoomId(), dto);

    LOGGER.info("✉️ 메시지 전송됨: roomId={}, senderId={}, content={}",
        dto.getRoomId(), dto.getSenderId(), dto.getMessage());
  }
}
