package com.highfive.meetu.domain.chat.personal.dto;

import com.highfive.meetu.domain.chat.common.entity.ChatMessage;
import com.highfive.meetu.domain.chat.common.entity.ChatRoom;
import com.highfive.meetu.domain.user.common.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WebSocket 채팅 메시지 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDTO {

  private String roomId;
  private Long senderId;
  private String senderName;
  private Integer senderType;
  private String message;
  private String type; // "TALK", "ENTER", "LEAVE"

  /**
   * DTO → Entity 변환
   */
  public ChatMessage toEntity(ChatRoom chatRoom, Account sender) {
    return ChatMessage.builder()
        .chatRoom(chatRoom)
        .sender(sender)
        .senderType(this.senderType)
        .message(this.message)
        .isRead(ChatMessage.ReadStatus.UNREAD)
        .build();
  }

  /**
   * Entity → DTO 변환
   */
  public static ChatMessageDTO from(ChatMessage entity) {
    return ChatMessageDTO.builder()
        .roomId(entity.getChatRoom().getId().toString())
        .senderId(entity.getSender().getId())
        .senderName(entity.getSender().getName())
        .senderType(entity.getSenderType())
        .message(entity.getMessage())
        .type("TALK") // 기본은 TALK로 설정 (필요시 조정)
        .build();
  }
}
