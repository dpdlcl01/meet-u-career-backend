package com.highfive.meetu.domain.auth.common.service.findid;

import com.highfive.meetu.domain.auth.common.dto.findid.FindIdRequestDTO;
import com.highfive.meetu.domain.auth.common.dto.findid.FindIdResponseDTO;
import com.highfive.meetu.domain.user.common.entity.Account;
import com.highfive.meetu.domain.user.common.repository.AccountRepository;
import com.highfive.meetu.global.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * FindIdService 구현체
 */
@Service
@RequiredArgsConstructor
public class FindIdServiceImpl implements FindIdService {

  private final AccountRepository accountRepository;

  @Override
  @Transactional(readOnly = true)
  public FindIdResponseDTO findUserId(FindIdRequestDTO dto) {
    // 이름과 이메일, 그리고 status=0 (ACTIVE)인 계정 조회
    Account account = accountRepository.findByNameAndEmailAndStatus(dto.getName(), dto.getEmail(), 0)
        .orElseThrow(() -> new NotFoundException("일치하는 사용자를 찾을 수 없습니다."));
    // userId 반환
    return FindIdResponseDTO.builder()
        .userId(account.getUserId())
        .build();
  }
}