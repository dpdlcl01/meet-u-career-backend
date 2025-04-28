package com.highfive.meetu.domain.chat.personal.service;

import com.highfive.meetu.domain.chat.common.entity.ChatRoom;
import com.highfive.meetu.domain.chat.common.repository.ChatRoomRepository;
import com.highfive.meetu.domain.chat.personal.dto.ChatRoomDTO;
import com.highfive.meetu.domain.company.common.entity.Company;
import com.highfive.meetu.domain.company.common.repository.CompanyRepository;
import com.highfive.meetu.domain.resume.common.entity.Resume;
import com.highfive.meetu.domain.resume.common.repository.ResumeRepository;
import com.highfive.meetu.domain.user.common.entity.Account;
import com.highfive.meetu.domain.user.common.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatRoomService {

  private final ChatRoomRepository chatRoomRepository;
  private final CompanyRepository companyRepository;
  private final AccountRepository accountRepository;
  private final ResumeRepository resumeRepository;

  /** 🔥 채팅방 생성 (DTO -> Entity 변환) */
  public Long createRoom(ChatRoomDTO dto) {
    Company company = companyRepository.findById(dto.getCompanyId()).orElseThrow();
    Account businessAccount = accountRepository.findById(dto.getBusinessAccountId()).orElseThrow();
    Account personalAccount = accountRepository.findById(dto.getPersonalAccountId()).orElseThrow();
    Resume resume = dto.getResumeId() != null ? resumeRepository.findById(dto.getResumeId()).orElse(null) : null;

    ChatRoom room = ChatRoom.builder()
        .company(company)
        .businessAccount(businessAccount)
        .personalAccount(personalAccount)
        .resume(resume)
        .status(ChatRoom.Status.OPEN)
        .build();
    return chatRoomRepository.save(room).getId();
  }

  /** 🔥 내 채팅방 목록 조회 */
  public List<ChatRoomDTO> findMyChatRooms(Long accountId) {
    List<ChatRoom> rooms = chatRoomRepository.findMyRooms(accountId);
    return rooms.stream().map(ChatRoomDTO::fromEntity).collect(Collectors.toList());
  }

  /** 🔥 채팅방 찾거나 없으면 생성 */
  public Long findOrCreateRoom(ChatRoomDTO dto) {
    Optional<ChatRoom> optionalRoom = chatRoomRepository.findRoom(
        dto.getCompanyId(),
        dto.getBusinessAccountId(),
        dto.getPersonalAccountId(),
        dto.getResumeId()
    );

    if (optionalRoom.isPresent()) {
      return optionalRoom.get().getId();
    } else {
      return createRoom(dto);
    }
  }
}