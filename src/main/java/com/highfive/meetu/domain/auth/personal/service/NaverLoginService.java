package com.highfive.meetu.domain.auth.personal.service;

import com.highfive.meetu.domain.auth.personal.dto.LoginResponseDTO;
import com.highfive.meetu.domain.auth.personal.dto.NaverTokenResponseDTO;
import com.highfive.meetu.domain.auth.personal.dto.NaverUserResponse;
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
public class NaverLoginService {

    @Value("${api.naver.client-id}")
    private String clientId;

    @Value("${api.naver.client-secret}")
    private String clientSecret;

    @Value("${api.naver.redirect-uri}")
    private String redirectUri;

    @Value("${api.naver.token-uri}")
    private String tokenUri;

    @Value("${api.naver.user-info-uri}")
    private String userInfoUri;

    private final AccountRepository accountRepository;
    private final ProfileRepository profileRepository;
    private final JwtProvider jwtProvider;

    /**
     * 네이버 로그인 또는 회원가입 처리 메서드
     * - 기존 회원이면 로그인
     * - 없으면 회원가입 후 로그인
     */
    public LoginResponseDTO naverLoginOrSignup(String code, String state) {
        // 1. 네이버 AccessToken 요청
        String naverAccessToken = requestAccessToken(code, state);

        // 2. 네이버 사용자 정보 요청
        NaverUserResponse user = getUserInfo(naverAccessToken);

        // 3. 이메일, 이름 추출
        String email = user.getResponse().getEmail();
        String name = user.getResponse().getName();

        // 4. 이메일로 계정 조회 또는 신규 가입
        Account account = accountRepository.findByEmailAndAccountType(email, 0)
            .orElseGet(() -> naverSignup(user));

        // 5. JWT 토큰 발급
        String accessToken = jwtProvider.generateAccessToken(account.getId());
        String refreshToken = jwtProvider.generateRefreshToken(account.getId());

        return new LoginResponseDTO(accessToken, refreshToken);
    }

    /**
     * 신규 네이버 회원 가입 처리
     */
    private Account naverSignup(NaverUserResponse user) {
        // 1. Account 생성 및 저장
        Account account = Account.builder()
            .email(user.getResponse().getEmail())
            .name(user.getResponse().getName())
            .phone("010-0000-0000")  // 기본값
            .birthday(LocalDate.of(2000, 1, 1))  // 기본값
            .accountType(0) // 개인회원
            .oauthProvider(3) // 3: 네이버
            .oauthId(String.valueOf(user.getId())) // (주의) 네이버 id는 response 안에 따로 있는 경우도 있음
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

    /**
     * 네이버 AccessToken 요청 메서드
     */
    private String requestAccessToken(String code, String state) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);
        params.add("state", state);

        HttpEntity<?> request = new HttpEntity<>(params, headers);
        ResponseEntity<NaverTokenResponseDTO> response = restTemplate.postForEntity(tokenUri, request, NaverTokenResponseDTO.class);

        return response.getBody().getAccess_token();
    }

    /**
     * 네이버 사용자 정보 조회 메서드
     */
    private NaverUserResponse getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<NaverUserResponse> response = restTemplate.exchange(userInfoUri, HttpMethod.GET, request, NaverUserResponse.class);

        return response.getBody();
    }
}
