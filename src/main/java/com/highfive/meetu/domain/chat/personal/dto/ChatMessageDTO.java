package com.highfive.meetu.domain.chat.personal.dto;

import com.highfive.meetu.domain.chat.common.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
  private Long id;
  private Long roomId;
  private Long senderId;
  private Integer senderType;
  private String senderName;
  private String message;
  private Integer isRead;
  private String type;
  private Long companyId;
  private Long businessAccountId;
  private Long personalAccountId;
  private Long resumeId;

  public static ChatMessageDTO fromEntity(ChatMessage entity) {
    return ChatMessageDTO.builder()
        .id(entity.getId())
        .roomId(entity.getChatRoom().getId())
        .senderId(entity.getSender().getId())
        .senderType(entity.getSenderType())
        .message(entity.getMessage())
        .isRead(entity.getIsRead())
        .build();
  }
}
