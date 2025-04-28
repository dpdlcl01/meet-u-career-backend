package com.highfive.meetu.domain.chat.personal.dto;

import com.highfive.meetu.domain.chat.common.entity.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDTO {
  private Long id;
  private Long companyId;
  private Long businessAccountId;
  private Long personalAccountId;
  private Long resumeId;
  private Integer status;

  public static ChatRoomDTO fromEntity(ChatRoom entity) {
    return ChatRoomDTO.builder()
        .id(entity.getId())
        .companyId(entity.getCompany().getId())
        .businessAccountId(entity.getBusinessAccount().getId())
        .personalAccountId(entity.getPersonalAccount().getId())
        .resumeId(entity.getResume() != null ? entity.getResume().getId() : null)
        .status(entity.getStatus())
        .build();
  }

  public static ChatRoomDTO fromMessageDTO(ChatMessageDTO dto) {
    return ChatRoomDTO.builder()
        .id(dto.getRoomId())
        .companyId(dto.getCompanyId())
        .businessAccountId(dto.getBusinessAccountId())
        .personalAccountId(dto.getPersonalAccountId())
        .resumeId(dto.getResumeId())
        .status(0)
        .build();
  }
}
