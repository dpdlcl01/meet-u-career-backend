package com.highfive.meetu.domain.chat.personal.service;

import com.highfive.meetu.domain.chat.common.entity.ChatMessage;
import com.highfive.meetu.domain.chat.common.entity.ChatRoom;
import com.highfive.meetu.domain.chat.common.repository.ChatMessageRepository;
import com.highfive.meetu.domain.chat.common.repository.ChatRoomRepository;
import com.highfive.meetu.domain.chat.personal.dto.ChatMessageDTO;
import com.highfive.meetu.domain.user.common.entity.Account;
import com.highfive.meetu.domain.user.common.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

  private final ChatMessageRepository chatMessageRepository;
  private final ChatRoomRepository chatRoomRepository;
  private final AccountRepository accountRepository;

  @Transactional
  public ChatMessage saveMessage(ChatMessageDTO dto) {
    // 채팅방 조회
    ChatRoom chatRoom = chatRoomRepository.findById(Long.parseLong(dto.getRoomId()))
        .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

    // 보낸 사람 계정 조회
    Account sender = accountRepository.findById(dto.getSenderId())
        .orElseThrow(() -> new IllegalArgumentException("보낸 사람 계정을 찾을 수 없습니다."));

    // ✅ DTO → Entity 변환 (DTO 내부 메서드 사용)
    ChatMessage message = dto.toEntity(chatRoom, sender);

    // 저장 후 반환
    return chatMessageRepository.save(message);
  }
  @Transactional
  public void markAllMessagesAsRead(Long roomId, Long accountId) {
    List<ChatMessage> unreadMessages = chatMessageRepository
        .findUnreadMessagesByRoomIdAndReceiverId(roomId, accountId);

    for (ChatMessage message : unreadMessages) {
      message.setIsRead(ChatMessage.ReadStatus.READ);
    }
  }
  public List<ChatMessageDTO> getMessagesByRoom(Long roomId) {
    List<ChatMessage> messages = chatMessageRepository.findByChatRoomIdOrderByCreatedAtAsc(roomId);
    return messages.stream()
        .map(ChatMessageDTO::from)
        .collect(Collectors.toList());
  }

}
