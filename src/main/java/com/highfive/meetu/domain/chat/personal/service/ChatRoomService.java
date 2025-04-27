package com.highfive.meetu.domain.chat.personal.service;

import com.highfive.meetu.domain.chat.common.entity.ChatRoom;
import com.highfive.meetu.domain.chat.common.repository.ChatRoomRepository;
import com.highfive.meetu.domain.chat.common.repository.ChatMessageRepository;
import com.highfive.meetu.domain.chat.personal.dto.ChatRoomDTO;
import com.highfive.meetu.domain.company.common.entity.Company;
import com.highfive.meetu.domain.company.common.repository.CompanyRepository;
import com.highfive.meetu.domain.user.common.entity.Account;
import com.highfive.meetu.domain.user.common.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

  private final ChatRoomRepository chatRoomRepository;
  private final ChatMessageRepository chatMessageRepository;
  private final CompanyRepository companyRepository;
  private final AccountRepository accountRepository;

  /**
   * 🔥 내 채팅방 목록 조회 (accountId 기준)
   */
  public List<ChatRoomDTO> findMyChatRooms(Long accountId) {
    List<ChatRoom> rooms = chatRoomRepository.findByBusinessAccountIdOrPersonalAccountId(accountId, accountId);

    return rooms.stream()
        .map(room -> {
          int unreadCount = chatMessageRepository.countByChatRoomIdAndIsReadAndSenderIdNot(
              room.getId(), 0, accountId
          );
          return ChatRoomDTO.from(room, unreadCount);
        })
        .collect(Collectors.toList());
  }

  /**
   * 🔥 채팅방이 존재하지 않으면 생성
   */
  public ChatRoom findOrCreateRoom(ChatRoomDTO dto) {
    return chatRoomRepository.findByBusinessAccountIdAndPersonalAccountId(dto.getBusinessAccountId(), dto.getPersonalAccountId())
        .orElseGet(() -> {
          Company company = companyRepository.findById(dto.getCompanyId())
              .orElseThrow(() -> new IllegalArgumentException("회사를 찾을 수 없습니다."));
          Account businessAccount = accountRepository.findById(dto.getBusinessAccountId())
              .orElseThrow(() -> new IllegalArgumentException("기업 계정을 찾을 수 없습니다."));
          Account personalAccount = accountRepository.findById(dto.getPersonalAccountId())
              .orElseThrow(() -> new IllegalArgumentException("개인 계정을 찾을 수 없습니다."));

          return chatRoomRepository.save(ChatRoom.builder()
              .company(company)
              .businessAccount(businessAccount)
              .personalAccount(personalAccount)
              .status(0)
              .build());
        });
  }
}
