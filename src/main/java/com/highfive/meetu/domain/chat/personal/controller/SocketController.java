package com.highfive.meetu.domain.chat.personal.controller;

import com.highfive.meetu.domain.chat.personal.dto.ChatMessageDTO;
import com.highfive.meetu.domain.chat.personal.dto.ChatRoomDTO;
import com.highfive.meetu.domain.chat.personal.service.ChatMessageService;
import com.highfive.meetu.domain.chat.personal.service.ChatRoomService;
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
  private final ChatRoomService chatRoomService;

  /**
   * WebSocket 연결 감지 (클라이언트가 소켓 연결 시)
   */
  @EventListener
  public void handleWebSocketConnectListener(SessionConnectEvent event) {
    LOGGER.info("📡 WebSocket 연결 감지됨");
  }

  /**
   * WebSocket 연결 종료 감지 (클라이언트가 소켓 종료 시)
   */
  @EventListener
  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
    LOGGER.info("📴 연결 종료됨 - sessionId={}", accessor.getSessionId());
  }

  /**
   * 클라이언트로부터 채팅 메시지를 수신하면 처리하는 메서드
   */
  @MessageMapping("/chat/send")
  public void sendMessage(ChatMessageDTO dto) {

    // ✅ 채팅방이 존재하지 않으면 생성
    ChatRoomDTO chatRoomDTO = ChatRoomDTO.from(dto);
    chatRoomService.findOrCreateRoom(chatRoomDTO);

    // 💬 메시지 타입 분기 처리
    switch (dto.getType()) {
      case "ENTER":
        dto.setMessage(dto.getSenderName() + "님이 입장하셨습니다.");
        break;
      case "LEAVE":
        dto.setMessage(dto.getSenderName() + "님이 퇴장하셨습니다.");
        break;
      case "TALK":
        chatMessageService.saveMessage(dto);
        break;
    }

    // 📤 채팅방 구독자들에게 메시지 전송
    messagingTemplate.convertAndSend("/sub/chat/" + dto.getRoomId(), dto);
  }
}
