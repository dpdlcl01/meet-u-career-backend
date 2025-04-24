package com.highfive.meetu.domain.auth.common.service.findpassword;

import com.highfive.meetu.domain.auth.common.dto.findpassword.FindPasswordRequestDTO;
import com.highfive.meetu.domain.auth.common.dto.findpassword.FindPasswordResponseDTO;
import com.highfive.meetu.domain.auth.common.dto.findpassword.ResetPasswordRequestDTO;
import com.highfive.meetu.domain.auth.common.dto.findpassword.ResetPasswordResponseDTO;
import com.highfive.meetu.domain.auth.common.dto.findpassword.VerifyCodeRequestDTO;
import com.highfive.meetu.domain.auth.common.dto.findpassword.VerifyCodeResponseDTO;
import com.highfive.meetu.domain.auth.common.service.email.EmailService;
import com.highfive.meetu.domain.user.common.entity.Account;
import com.highfive.meetu.domain.user.common.repository.AccountRepository;
import com.highfive.meetu.global.common.exception.BadRequestException;
import com.highfive.meetu.global.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 비밀번호 재설정 로직 구현체
 */
@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {

  private final AccountRepository accountRepository; // 사용자 조회/저장
  private final TokenService tokenService; // JWT 토큰 생성·검증
  private final EmailService emailService; // 인증 코드 이메일 전송
  private final PasswordEncoder passwordEncoder; // 비밀번호 해시

  @Override
  @Transactional(readOnly = true)
  public FindPasswordResponseDTO requestPasswordReset(FindPasswordRequestDTO dto) {
    // 1) 사용자 조회 (email, name, userId 일치)
    Account account = accountRepository
        .findByEmailAndNameAndUserIdAndStatus(dto.getEmail(), dto.getName(), dto.getUserId(), Account.Status.ACTIVE)
        .orElseThrow(() -> new NotFoundException("일치하는 사용자를 찾을 수 없습니다."));

    // 2) OAuth 계정 처리
    if (account.getOauthProvider() != null) {
      return FindPasswordResponseDTO.builder()
          .sent(false)
          .socialProvider(account.getOauthProvider().toString())
          .email(account.getEmail())
          .build();
    }

    // 3) 일반 계정: 코드 생성 → 이메일 전송 → 토큰 생성
    String code = String.format("%06d", (int) (Math.random() * 1_000_000));
    emailService.sendPasswordResetCode(account.getEmail(), code);

    String token = tokenService.generatePasswordResetToken(account.getEmail(), code);
    return FindPasswordResponseDTO.builder()
        .sent(true)
        .verificationToken(token)
        .build();
  }

  @Override
  public VerifyCodeResponseDTO verifyCode(VerifyCodeRequestDTO dto) {
    // 1) 토큰 파싱·검증 → 페이로드 추출
    PasswordResetPayload payload = tokenService.parseAndValidatePasswordResetToken(dto.getVerificationToken());

    // 2) 코드 일치 확인
    if (!payload.getCode().equals(dto.getCode())) {
      throw new BadRequestException("인증 코드가 일치하지 않습니다.");
    }
    return VerifyCodeResponseDTO.builder()
        .verified(true)
        .build();
  }

  @Override
  @Transactional
  public ResetPasswordResponseDTO resetPassword(ResetPasswordRequestDTO dto) {
    // 1) 코드 재검증
    PasswordResetPayload payload = tokenService.parseAndValidatePasswordResetToken(dto.getVerificationToken());
    if (!payload.getCode().equals(dto.getCode())) {
      throw new BadRequestException("인증 코드가 일치하지 않습니다.");
    }

    // 2) 사용자 조회
    Account account = accountRepository.findByEmail(payload.getEmail())
        .orElseThrow(() -> new NotFoundException("사용자 정보를 찾을 수 없습니다."));

    // 3) 비밀번호 해시 후 업데이트
    account.setPassword(passwordEncoder.encode(dto.getNewPassword()));
    // JPA 영속성 컨텍스트가 변경 감지 후 update 수행

    return ResetPasswordResponseDTO.builder()
        .reset(true)
        .build();
  }
}