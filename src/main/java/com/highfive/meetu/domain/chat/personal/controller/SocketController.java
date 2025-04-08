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
    switch (dto.getType()) {
      case "ENTER":
        // 입장 메시지 로그 남기기 or 시스템 메시지 생성
        dto.setMessage(dto.getSenderName() + "님이 입장하셨습니다.");
        break;
      case "LEAVE":
        // 퇴장 메시지
        dto.setMessage(dto.getSenderName() + "님이 퇴장하셨습니다.");
        break;
      case "TALK":
        // 일반 채팅 메시지 저장
        chatMessageService.saveMessage(dto);
        break;
    }

    // 메시지를 구독자에게 전송
    messagingTemplate.convertAndSend("/sub/chat/" + dto.getRoomId(), dto);
  }


}
