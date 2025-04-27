package com.highfive.meetu.domain.chat.personal.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

/**
 * WebSocket 알림 PUSH용 DTO
 * (메시지 보낼 때, 퇴장 알림 보낼 때 등 공통 사용)
 */
@Getter
@Builder
public class ChatNotificationDTO {

  private String type;         // 알림 타입 ("NEW_MESSAGE", "EXIT" 등)
  private String roomId;       // 채팅방 ID
  private Long senderId;       // 보낸 사람 ID
  private String senderName;   // 보낸 사람 이름 (optional)
  private String message;      // 메시지 본문
  private LocalDateTime timestamp; // 전송 시간
}
