package com.highfive.meetu.domain.chat.personal.dto;

import com.highfive.meetu.domain.chat.common.entity.ChatRoom;
import com.highfive.meetu.domain.company.common.entity.Company;
import com.highfive.meetu.domain.resume.common.entity.Resume;
import com.highfive.meetu.domain.user.common.entity.Account;
import lombok.Builder;
import lombok.Getter;

/**
 * 채팅방 생성을 위한 DTO
 * - ChatMessageDTO로부터 필요한 정보만 분리해 사용
 * - toEntity 메서드로 실제 ChatRoom 엔티티 변환 가능
 */
@Getter
@Builder
public class ChatRoomDTO {

  private Long companyId;
  private Long businessAccountId;
  private Long personalAccountId;
  private Long resumeId; // optional

  /**
   * ChatMessageDTO → ChatRoomDTO 변환
   */
  public static ChatRoomDTO from(ChatMessageDTO dto) {
    return ChatRoomDTO.builder()
        .companyId(dto.getCompanyId())
        .businessAccountId(dto.getBusinessAccountId())
        .personalAccountId(dto.getPersonalAccountId())
        .resumeId(dto.getResumeId())
        .build();
  }

  /**
   * ChatRoomDTO → ChatRoom Entity 변환
   */
  public ChatRoom toEntity(Company company, Account businessAccount, Account personalAccount, Resume resume) {
    return ChatRoom.builder()
        .company(company)
        .businessAccount(businessAccount)
        .personalAccount(personalAccount)
        .resume(resume)
        .status(ChatRoom.Status.OPEN)
        .build();
  }
}
