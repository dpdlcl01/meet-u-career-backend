package com.highfive.meetu.domain.chat.personal.dto;

import com.highfive.meetu.domain.chat.common.entity.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 🔥 채팅방 조회용 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomDTO {

  private Long id;                 // 채팅방 ID
  private Long companyId;           // 기업 ID
  private Long businessAccountId;   // 기업 계정 ID
  private Long personalAccountId;   // 개인 계정 ID
  private int unreadCount;          // 안 읽은 메시지 수

  /**
   * ChatRoom 엔티티를 DTO로 변환 + 안읽은 메시지 수 포함
   */
  public static ChatRoomDTO from(ChatRoom chatRoom, int unreadCount) {
    return ChatRoomDTO.builder()
        .id(chatRoom.getId())
        .companyId(chatRoom.getCompany().getId())
        .businessAccountId(chatRoom.getBusinessAccount().getId())
        .personalAccountId(chatRoom.getPersonalAccount().getId())
        .unreadCount(unreadCount)
        .build();
  }

  /**
   * 채팅 메시지 수신 시 채팅방 정보를 DTO로 변환
   * (businessAccountId, personalAccountId, companyId만 필요)
   */
  public static ChatRoomDTO fromMessageDTO(ChatMessageDTO messageDTO) {
    return ChatRoomDTO.builder()
        .companyId(messageDTO.getCompanyId())
        .businessAccountId(messageDTO.getBusinessAccountId())
        .personalAccountId(messageDTO.getSenderId())
        .build();
  }
}
