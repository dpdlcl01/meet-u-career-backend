package com.highfive.meetu.domain.chat.personal.service;


import com.highfive.meetu.domain.chat.common.entity.ChatRoom;
import com.highfive.meetu.domain.chat.common.repository.ChatRoomRepository;
import com.highfive.meetu.domain.chat.personal.dto.ChatRoomDTO;
import com.highfive.meetu.domain.company.common.entity.Company;
import com.highfive.meetu.domain.resume.common.entity.Resume;
import com.highfive.meetu.domain.user.common.entity.Account;
import com.highfive.meetu.domain.company.common.repository.CompanyRepository;
import com.highfive.meetu.domain.resume.common.repository.ResumeRepository;
import com.highfive.meetu.domain.user.common.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

  private final ChatRoomRepository chatRoomRepository;
  private final AccountRepository accountRepository;
  private final CompanyRepository companyRepository;
  private final ResumeRepository resumeRepository;

  /**
   * 기업 담당자 + 구직자 조합으로 기존 채팅방이 있으면 반환, 없으면 새로 생성해서 저장
   */
  @Transactional
  public ChatRoom findOrCreateRoom(ChatRoomDTO dto) {
    return chatRoomRepository.findByBusinessAccountIdAndPersonalAccountId(
        dto.getBusinessAccountId(), dto.getPersonalAccountId()
    ).orElseGet(() -> {
      Account businessAccount = accountRepository.findById(dto.getBusinessAccountId())
          .orElseThrow(() -> new IllegalArgumentException("❌ 채용 담당자 없음"));

      Account personalAccount = accountRepository.findById(dto.getPersonalAccountId())
          .orElseThrow(() -> new IllegalArgumentException("❌ 구직자 없음"));

      Company company = companyRepository.findById(dto.getCompanyId())
          .orElseThrow(() -> new IllegalArgumentException("❌ 회사 없음"));

      Resume resume = null;
      if (dto.getResumeId() != null) {
        resume = resumeRepository.findById(dto.getResumeId()).orElse(null);
      }

      ChatRoom newRoom = dto.toEntity(company, businessAccount, personalAccount, resume);
      return chatRoomRepository.save(newRoom);
    });
  }
}
