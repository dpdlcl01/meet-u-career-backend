package com.highfive.meetu.domain.auth.personal.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.highfive.meetu.domain.auth.personal.dto.GoogleTokenResponse;
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
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class GoogleLoginService {

    @Value("${api.google.client-id}")
    private String clientId;

    @Value("${api.google.client-secret}")
    private String clientSecret;

    @Value("${api.google.redirect-uri}")
    private String redirectUri;

    private final AccountRepository accountRepository;
    private final ProfileRepository profileRepository;
    private final JwtProvider jwtProvider;

    /**
     * 구글 로그인 처리 메서드
     * - Authorization Code를 받아서
     * - AccessToken + IdToken 교환 후
     * - 사용자 정보 검증 및 회원가입/로그인 처리
     */
    public LoginResponseDTO login(String authorizationCode) {
        // 1. Authorization Code를 사용해서 AccessToken + IdToken 교환
        GoogleTokenResponse tokenResponse = requestGoogleToken(authorizationCode);

        // 2. IdToken 검증
        GoogleIdToken.Payload payload = verifyIdToken(tokenResponse.getId_token());

        String email = payload.getEmail();
        String name = (String) payload.get("name");

        // 3. 이메일로 Account 조회 또는 신규 생성
        Account account = accountRepository.findByEmailAndAccountType(email, 0)
            .orElseGet(() -> googleSignup(email, name));

        // 4. JWT 토큰 발급
        String accessToken = jwtProvider.generateAccessToken(account.getId());
        String refreshToken = jwtProvider.generateRefreshToken(account.getId());

        return new LoginResponseDTO(accessToken, refreshToken);
    }

    /**
     * 구글 Authorization Code를 AccessToken + IdToken으로 교환하는 메서드
     */
    private GoogleTokenResponse requestGoogleToken(String code) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<GoogleTokenResponse> response = restTemplate.postForEntity(
            "https://oauth2.googleapis.com/token", request, GoogleTokenResponse.class
        );

        return response.getBody();
    }

    /**
     * 구글 IdToken을 검증하는 메서드
     */
    private GoogleIdToken.Payload verifyIdToken(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance()
            )
                .setAudience(Collections.singletonList(clientId))
                .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new RuntimeException("Invalid ID Token");
            }

            return idToken.getPayload();
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify ID Token", e);
        }
    }

    /**
     * 구글 신규 회원 가입 처리
     */
    private Account googleSignup(String email, String name) {
        Account account = Account.builder()
            .email(email)
            .name(name)
            .phone("010-0000-0000") // 기본값
            .birthday(LocalDate.of(2000, 1, 1)) // 기본값
            .accountType(0) // 개인회원
            .oauthProvider(1) // 1: 구글
            .oauthId(email) // 이메일을 고유 식별자로 사용
            .status(0) // 활성
            .build();

        Account savedAccount = accountRepository.save(account);

        Profile profile = Profile.builder()
            .account(savedAccount)
            .build();
        profileRepository.save(profile);

        return savedAccount;
    }
}
