package com.highfive.meetu.domain.auth.common.service.login;

import com.highfive.meetu.domain.auth.personal.dto.LoginRequestDTO;
import com.highfive.meetu.domain.auth.personal.dto.LoginResponseDTO;
import com.highfive.meetu.domain.auth.personal.service.RefreshTokenService;
import com.highfive.meetu.domain.user.common.entity.Account;
import com.highfive.meetu.domain.user.common.entity.Admin;
import com.highfive.meetu.domain.user.common.repository.AccountRepository;
import com.highfive.meetu.domain.user.common.repository.AdminRepository;
import com.highfive.meetu.global.common.exception.BadRequestException;
import com.highfive.meetu.global.common.exception.NotFoundException;
import com.highfive.meetu.global.common.response.ResultData;
import com.highfive.meetu.infra.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * 로그인 처리 서비스
 * - 개인, 기업, 관리자 로그인 처리
 * - 토큰 발급 및 Redis 저장, 쿠키 응답 구성
 */
@Service
@RequiredArgsConstructor
public class LoginService {

    private final AccountRepository accountRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    /**
     * 개인회원 로그인 처리
     */
    public ResponseEntity<ResultData<LoginResponseDTO>> userLogin(LoginRequestDTO dto) {
        Account account = accountRepository.findByUserIdAndAccountType(dto.getUserId(), 0)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 아이디입니다."));

        return handleLogin(
            account.getId(),
            account.getPassword(),
            dto.getPassword(),
            false, // checkStatus
            null,  // status
            false  // isAdmin
        );
    }

    /**
     * 기업회원 로그인 처리
     * - 상태값 확인 포함
     */
    public ResponseEntity<ResultData<LoginResponseDTO>> businessLogin(LoginRequestDTO dto) {
        Account account = accountRepository.findByUserIdAndAccountType(dto.getUserId(), 1)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 아이디입니다."));

        return handleLogin(
            account.getId(),
            account.getPassword(),
            dto.getPassword(),
            true,              // checkStatus
            account.getStatus(), // 상태값 (0:정상, 1~3은 오류 처리)
            false              // isAdmin
        );
    }

    /**
     * 관리자 로그인 처리
     */
    public ResponseEntity<ResultData<LoginResponseDTO>> adminLogin(LoginRequestDTO dto) {
        Admin admin = adminRepository.findByEmail(dto.getUserId())
            .orElseThrow(() -> new NotFoundException("존재하지 않는 이메일입니다."));

        return handleLogin(
            admin.getId(),
            admin.getPassword(),
            dto.getPassword(),
            false, // checkStatus
            null,  // status
            true   // isAdmin
        );
    }

    /**
     * 로그인 공통 처리 로직
     *
     * @param id             로그인 대상 ID
     * @param encodedPassword 저장된 암호화된 비밀번호
     * @param rawPassword    입력된 비밀번호
     * @param checkStatus    기업회원 상태값 검사 여부
     * @param status         계정 상태값
     * @param isAdmin        관리자 여부
     */
    private ResponseEntity<ResultData<LoginResponseDTO>> handleLogin(
        Long id,
        String encodedPassword,
        String rawPassword,
        boolean checkStatus,
        Integer status,
        boolean isAdmin
    ) {
        // 상태값 검사 (기업회원만)
        if (checkStatus && status != null) {
            switch (status) {
                case 1:
                    return ResponseEntity.status(403)
                        .body(ResultData.fail("비활성화된 계정입니다. 관리자에게 문의하세요."));
                case 2:
                    return ResponseEntity.status(403)
                        .body(ResultData.fail("승인 대기 중입니다. 관리자의 승인을 기다려 주세요."));
                case 3:
                    return ResponseEntity.status(403)
                        .body(ResultData.fail("반려된 계정입니다. 메일의 반려 사유를 확인해 주세요."));
            }
        }

        // 비밀번호 일치 확인
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new BadRequestException("비밀번호가 일치하지 않습니다.");
        }

        // 관리자 여부에 따라 토큰 발급 방식 분기
        String accessToken = isAdmin
            ? jwtProvider.generateAdminAccessToken(id)
            : jwtProvider.generateAccessToken(id);

        String refreshToken = isAdmin
            ? jwtProvider.generateAdminRefreshToken(id)
            : jwtProvider.generateRefreshToken(id);

        return generateTokenPair(id, accessToken, refreshToken);
    }

    /**
     * AccessToken, RefreshToken 발급 후 응답 생성
     */
    private ResponseEntity<ResultData<LoginResponseDTO>> generateTokenPair(
        Long id,
        String accessToken,
        String refreshToken
    ) {
        // Redis에 RefreshToken 저장
        refreshTokenService.saveRefreshToken(
            id,
            refreshToken,
            jwtProvider.getRefreshTokenExpiration()
        );

        // AccessToken 쿠키
        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", accessToken)
            .httpOnly(true)
            .secure(false)
            .path("/")
            .sameSite("Strict")
            .maxAge(Duration.ofMillis(jwtProvider.getAccessTokenExpiration()))
            .build();

        // RefreshToken 쿠키
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
            .httpOnly(true)
            .secure(false)
            .path("/")
            .sameSite("Strict")
            .maxAge(Duration.ofMillis(jwtProvider.getRefreshTokenExpiration()))
            .build();

        // 응답 DTO 생성
        LoginResponseDTO responseDTO = new LoginResponseDTO(accessToken, refreshToken);

        return ResponseEntity.ok()
            .headers(headers -> {
                headers.add("Set-Cookie", accessTokenCookie.toString());
                headers.add("Set-Cookie", refreshTokenCookie.toString());
            })
            .body(ResultData.success(1, responseDTO));
    }
}
