package com.highfive.meetu.domain.chat.personal.service;

import com.highfive.meetu.domain.chat.common.entity.ChatMessage;
import com.highfive.meetu.domain.chat.common.entity.ChatRoom;
import com.highfive.meetu.domain.chat.common.repository.ChatMessageRepository;
import com.highfive.meetu.domain.chat.common.repository.ChatRoomRepository;
import com.highfive.meetu.domain.chat.personal.dto.ChatMessageDTO;
import com.highfive.meetu.domain.company.common.entity.Company;
import com.highfive.meetu.domain.company.common.repository.CompanyRepository;
import com.highfive.meetu.domain.user.common.entity.Account;
import com.highfive.meetu.domain.user.common.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

  private final ChatMessageRepository chatMessageRepository;
  private final ChatRoomRepository chatRoomRepository;
  private final AccountRepository accountRepository;
  private final CompanyRepository companyRepository;

  @Transactional
  public ChatMessage saveMessage(ChatMessageDTO dto) {
    ChatRoom chatRoom = chatRoomRepository.findById(Long.parseLong(dto.getRoomId()))
        .orElseThrow(() -> new IllegalArgumentException("채팅방 없음"));
    Account sender = accountRepository.findById(dto.getSenderId())
        .orElseThrow(() -> new IllegalArgumentException("보낸 사람 없음"));

    ChatMessage message = dto.toEntity(chatRoom, sender);
    return chatMessageRepository.save(message);
  }

  @Transactional
  public void markAllMessagesAsRead(Long roomId, Long accountId) {
    List<ChatMessage> unreadMessages = chatMessageRepository.findUnreadMessagesByRoomIdAndReceiverId(roomId, accountId);
    for (ChatMessage message : unreadMessages) {
      message.setIsRead(ChatMessage.ReadStatus.READ);
    }
  }

  @Transactional(readOnly = true)
  public List<ChatMessageDTO> getMessagesByRoom(Long roomId) {
    List<ChatMessage> messages = chatMessageRepository.findByChatRoomIdOrderByCreatedAtAsc(roomId);
    return messages.stream()
        .map(ChatMessageDTO::from)
        .collect(Collectors.toList());
  }

  /**
   * ✅ 3번: 마지막 메시지 가져오기
   */
  @Transactional(readOnly = true)
  public ChatMessageDTO getLastMessage(Long roomId) {
    Optional<ChatMessage> lastMessage = chatMessageRepository.findTopByChatRoomIdOrderByCreatedAtDesc(roomId);
    return lastMessage.map(ChatMessageDTO::from)
        .orElse(null);
  }
}
