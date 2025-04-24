package com.highfive.meetu.domain.auth.common.service.findpassword;

import com.highfive.meetu.global.common.exception.BadRequestException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;

/**
 * JWT 라이브러리(io.jsonwebtoken)를 사용한 TokenService 구현체
 */
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

  // application.yml에 설정한 비밀키
  @Value("${jwt.secret-key}")
  private String jwtSecret;

  // 비밀번호 재설정 토큰 만료 시간(ms)
  @Value("${jwt.passwordReset.expiration}")
  private long expirationMillis;

  private Key signingKey;

  @PostConstruct
  public void init() {
    // 비밀키를 Key 객체로 변환
    signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
  }

  @Override
  public String generatePasswordResetToken(String email, String code) {
    Date now = new Date();
    Date exp = new Date(now.getTime() + expirationMillis);

    return Jwts.builder()
        .setSubject(email) // sub: 이메일
        .claim("code", code) // custom claim: 인증 코드
        .claim("type", "PASSWORD_RESET") // custom claim: 토큰 용도
        .setIssuedAt(now) // iat
        .setExpiration(exp) // exp
        .signWith(signingKey, SignatureAlgorithm.HS256)// 서명
        .compact();
  }

  @Override
  public PasswordResetPayload parseAndValidatePasswordResetToken(String token) {
    try {
      Jws<Claims> parsed = Jwts.parserBuilder()
          .setSigningKey(signingKey)
          .build()
          .parseClaimsJws(token);

      Claims claims = parsed.getBody();

      // 토큰 용도 확인
      String type = claims.get("type", String.class);
      if (!"PASSWORD_RESET".equals(type)) {
        throw new BadRequestException("유효하지 않은 토큰 타입입니다.");
      }

      String email = claims.getSubject();
      String code = claims.get("code", String.class);

      return new PasswordResetPayload(email, code);

    } catch (JwtException | IllegalArgumentException e) {
      // 만료, 서명 오류 등 모두 BadRequestException으로 처리
      throw new BadRequestException("유효하지 않거나 만료된 토큰입니다.");
    }
  }
}
