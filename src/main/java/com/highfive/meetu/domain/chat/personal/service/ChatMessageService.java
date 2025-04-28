package com.highfive.meetu.domain.chat.personal.service;

import com.highfive.meetu.domain.chat.common.entity.ChatMessage;
import com.highfive.meetu.domain.chat.common.entity.ChatRoom;
import com.highfive.meetu.domain.chat.common.repository.ChatMessageRepository;
import com.highfive.meetu.domain.chat.personal.dto.ChatMessageDTO;
import com.highfive.meetu.domain.user.common.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatMessageService {

  private final ChatMessageRepository chatMessageRepository;
  private final ChatRoomService chatRoomService;
  private final AccountRepository accountRepository;

  /**
   * 🔥 채팅방의 메시지 목록 조회
   */
  public List<ChatMessageDTO> getMessagesByRoom(Long roomId) {
    List<ChatMessage> messages = chatMessageRepository.findByChatRoomIdOrderByCreatedAt(roomId);
    return messages.stream()
        .map(m -> ChatMessageDTO.builder()
            .roomId(m.getChatRoom().getId())
            .senderId(m.getSender().getId())
            .senderName(m.getSender().getName())
            .senderType(m.getSenderType())
            .message(m.getMessage())
            .type("TALK") // 고정
            .isRead(m.getIsRead())
            .companyId(m.getChatRoom().getCompany().getId())
            .businessAccountId(m.getChatRoom().getBusinessAccount().getId())
            .personalAccountId(m.getChatRoom().getPersonalAccount().getId())
            .resumeId(m.getChatRoom().getResume() != null ? m.getChatRoom().getResume().getId() : null)
            .build())
        .collect(Collectors.toList());
  }

  /**
   * 🔥 채팅 메시지 저장
   */
  @Transactional
  public void saveMessage(ChatMessageDTO dto) {
    ChatRoom chatRoom = chatRoomService.getEntityOrThrow(dto.getRoomId());
    ChatMessage message = ChatMessage.builder()
        .chatRoom(chatRoom)
        .sender(accountRepository.findById(dto.getSenderId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계정입니다.")))
        .senderType(dto.getSenderType())
        .message(dto.getMessage())
        .isRead(0) // 저장 시 기본은 안 읽음
        .build();
    chatMessageRepository.save(message);
  }
}
