package com.highfive.meetu.domain.auth.common.service.findpassword;

/**
 * JWT 기반 비밀번호 재설정 토큰 생성·검증 서비스
 */
public interface TokenService {

  /**
   * 이메일과 6자리 코드를 포함한 비밀번호 재설정용 JWT 토큰을 생성합니다.
   *
   * @param email 사용자 이메일
   * @param code  인증 코드
   * @return 서명된 JWT 토큰 문자열
   */
  String generatePasswordResetToken(String email, String code);

  /**
   * JWT 토큰을 검증하고, 페이로드에서 이메일과 코드를 추출합니다.
   * 토큰이 만료되었거나 위변조된 경우 BadRequestException을 던집니다.
   *
   * @param token 클라이언트가 전달한 JWT 토큰
   * @return 토큰에 담긴 이메일·코드 페이로드
   */
  PasswordResetPayload parseAndValidatePasswordResetToken(String token);
}
