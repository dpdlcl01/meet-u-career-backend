package com.highfive.meetu.domain.auth.common.service.email;

/**
 * 이메일 전송 기능 추상화 인터페이스
 */
public interface EmailService {

  /**
   * 비밀번호 재설정용 인증 코드를 포함한 이메일을 전송합니다.
   *
   * @param toEmail 수신자 이메일 주소
   * @param code    인증 코드 (6자리)
   */
  void sendPasswordResetCode(String toEmail, String code);
}
