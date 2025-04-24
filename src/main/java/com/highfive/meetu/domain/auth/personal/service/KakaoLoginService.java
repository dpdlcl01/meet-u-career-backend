package com.highfive.meetu.domain.auth.personal.service;

import com.highfive.meetu.domain.auth.personal.dto.KakaoTokenResponseDTO;
import com.highfive.meetu.domain.auth.personal.dto.KakaoUserResponse;
import com.highfive.meetu.domain.auth.personal.dto.LoginResponseDTO;
import com.highfive.meetu.domain.user.common.entity.Account;
import com.highfive.meetu.domain.user.common.entity.Profile;
import com.highfive.meetu.domain.user.common.repository.AccountRepository;
import com.highfive.meetu.domain.user.common.repository.ProfileRepository;
import com.highfive.meetu.infra.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class KakaoLoginService {

    @Value("${api.kakao.client-id}")
    private String clientId;

    @Value("${api.kakao.redirect-uri}")
    private String redirectUri;

    @Value("${api.kakao.token-uri}")
    private String tokenUri;

    @Value("${api.kakao.user-info-uri}")
    private String userInfoUri;

    private final AccountRepository accountRepository;
    private final ProfileRepository profileRepository;
    private final JwtProvider jwtProvider;

    /**
     * 카카오 Authorization Code를 사용해 AccessToken을 요청하는 메서드
     */
    public String requestAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        HttpEntity<?> request = new HttpEntity<>(params, headers);
        ResponseEntity<KakaoTokenResponseDTO> response = restTemplate.postForEntity(tokenUri, request, KakaoTokenResponseDTO.class);

        return response.getBody().getAccess_token();
    }

    /**
     * 카카오 AccessToken을 이용하여 사용자 정보를 요청하는 메서드
     */
    public KakaoUserResponse getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<KakaoUserResponse> response = restTemplate.exchange(userInfoUri, HttpMethod.GET, request, KakaoUserResponse.class);

        return response.getBody();
    }

    /**
     * 카카오 로그인 또는 회원가입 처리 메서드
     * - 기존 회원이면 로그인
     * - 없으면 회원가입 후 로그인
     */
    public LoginResponseDTO kakaoLoginOrSignup(String code) {
        // 1. 카카오 AccessToken 요청
        String kakaoAccessToken = requestAccessToken(code);

        // 2. 카카오 사용자 정보 요청
        KakaoUserResponse user = getUserInfo(kakaoAccessToken);

        // 3. 이메일 가져오기
        String email = user.getKakaoAccount().getEmail();

        // 4. 이메일로 계정 조회 또는 신규 가입
        Account account = accountRepository.findByEmailAndAccountType(email, 0)
            .orElseGet(() -> kakaoSignup(user));

        // 5. JWT 토큰 발급
        String accessToken = jwtProvider.generateAccessToken(account.getId());
        String refreshToken = jwtProvider.generateRefreshToken(account.getId());

        return new LoginResponseDTO(accessToken, refreshToken);
    }

    /**
     * 신규 카카오 회원 가입 처리
     */
    private Account kakaoSignup(KakaoUserResponse user) {
        // 1. Account 생성 및 저장
        Account account = Account.builder()
            .email(user.getKakaoAccount().getEmail())
            .name(user.getKakaoAccount().getProfile().getNickname())
            .phone("010-0000-0000")  // 기본값
            .birthday(LocalDate.of(2000, 1, 1))  // 기본값
            .accountType(0) // 개인회원
            .oauthProvider(2) // 2: 카카오
            .oauthId(String.valueOf(user.getId())) // 카카오 고유 ID
            .status(0) // 활성 상태
            .build();

        Account savedAccount = accountRepository.save(account);

        // 2. Profile 생성 및 저장
        Profile profile = Profile.builder()
            .account(savedAccount)
            .build();
        profileRepository.save(profile);

        return savedAccount;
    }
}
