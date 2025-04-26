package com.highfive.meetu.domain.user.personal.service;

import com.highfive.meetu.domain.user.common.entity.Account;
import com.highfive.meetu.domain.user.common.entity.Profile;
import com.highfive.meetu.domain.user.common.repository.AccountRepository;
import com.highfive.meetu.domain.user.common.repository.ProfileRepository;
import com.highfive.meetu.domain.user.personal.dto.PersonalSignUpRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonalAccountService {
    private final AccountRepository accountRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;

    // 아이디 중복 체크
    public boolean findByUserId(String userId) {
        return accountRepository.existsByUserId(userId);
    }

    // 이메일 중복 체크
    public boolean findByEmail(String email) {
        return accountRepository.existsByEmail(email);
    }

    // 개인회원 회원가입
    public PersonalSignUpRequestDTO save(PersonalSignUpRequestDTO dto) {
        // 비밀번호 암호화 후 Account 저장
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        Account savedAccount = accountRepository.save(dto.toEntity(encodedPassword));

        // Profile 엔티티 생성 및 저장
        Profile profile = Profile.builder()
            .account(savedAccount)
            .build();
        profileRepository.save(profile);

        return PersonalSignUpRequestDTO.fromEntity(savedAccount);
    }
}
