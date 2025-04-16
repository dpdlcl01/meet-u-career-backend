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
import java.util.stream.Collectors;

/**
 * 채팅 메시지 관련 서비스
 * - 메시지 저장
 * - 읽음 처리
 * - 메시지 목록 조회
 */
@Service
@RequiredArgsConstructor
public class ChatMessageService {

  private final ChatMessageRepository chatMessageRepository;
  private final ChatRoomRepository chatRoomRepository;
  private final AccountRepository accountRepository;
  private final CompanyRepository companyRepository;

  /**
   * 클라이언트로부터 받은 DTO를 DB에 저장
   * @param dto 채팅 메시지 DTO
   * @return 저장된 ChatMessage
   */
  @Transactional
  public ChatMessage saveMessage(ChatMessageDTO dto) {
    // 1️⃣ 채팅방 조회
    ChatRoom chatRoom = chatRoomRepository.findById(Long.parseLong(dto.getRoomId()))
        .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

    // 2️⃣ 보낸 사람 조회
    Account sender = accountRepository.findById(dto.getSenderId())
        .orElseThrow(() -> new IllegalArgumentException("보낸 사람 계정을 찾을 수 없습니다."));

    // 3️⃣ DTO → Entity 변환 및 저장
    ChatMessage message = dto.toEntity(chatRoom, sender);
    return chatMessageRepository.save(message);
  }

  /**
   * 해당 채팅방에서 내가 받은 메시지들을 모두 읽음 처리
   * @param roomId 채팅방 ID
   * @param accountId 로그인한 사용자 ID
   */
  @Transactional
  public void markAllMessagesAsRead(Long roomId, Long accountId) {
    List<ChatMessage> unreadMessages = chatMessageRepository
        .findUnreadMessagesByRoomIdAndReceiverId(roomId, accountId);

    for (ChatMessage message : unreadMessages) {
      message.setIsRead(ChatMessage.ReadStatus.READ);
    }
  }

  /**
   * 채팅방 입장 시 메시지 전체 불러오기 (최신순 정렬)
   * @param roomId 채팅방 ID
   * @return 메시지 DTO 목록
   */
  @Transactional(readOnly = true)
  public List<ChatMessageDTO> getMessagesByRoom(Long roomId) {
    List<ChatMessage> messages = chatMessageRepository.findByChatRoomIdOrderByCreatedAtAsc(roomId);
    return messages.stream()
        .map(ChatMessageDTO::from)
        .collect(Collectors.toList());
  }
  public void createRoomIfNotExist(ChatMessageDTO dto) {
    Long roomId = Long.parseLong(dto.getRoomId());

    boolean exists = chatRoomRepository.existsById(roomId);
    if (!exists) {
      Company company = companyRepository.findById(dto.getCompanyId())
          .orElseThrow(() -> new IllegalArgumentException("❌ 회사 없음"));

      Account business = accountRepository.findById(dto.getBusinessAccountId())
          .orElseThrow(() -> new IllegalArgumentException("❌ 채용 담당자 없음"));

      Account personal = accountRepository.findById(dto.getPersonalAccountId())
          .orElseThrow(() -> new IllegalArgumentException("❌ 구직자 없음"));

      ChatRoom room = ChatRoom.builder()
          .company(company)
          .businessAccount(business)
          .personalAccount(personal)
          .status(ChatRoom.Status.OPEN)
          .build();

      chatRoomRepository.save(room);
    }
  }


}
