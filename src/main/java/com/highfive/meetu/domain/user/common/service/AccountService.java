package com.highfive.meetu.domain.user.common.service;

import com.highfive.meetu.domain.auth.personal.type.Role;
import com.highfive.meetu.domain.user.common.entity.Account;
import com.highfive.meetu.domain.user.common.entity.Admin;
import com.highfive.meetu.domain.user.common.entity.Profile;
import com.highfive.meetu.domain.user.common.repository.AccountRepository;
import com.highfive.meetu.domain.user.common.repository.AdminRepository;
import com.highfive.meetu.domain.user.common.repository.ProfileRepository;
import com.highfive.meetu.domain.user.personal.dto.PersonalInfoDTO;
import com.highfive.meetu.global.common.exception.NotFoundException;
import com.highfive.meetu.global.security.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.highfive.meetu.global.common.response.ErrorCode.*;

/**
 * 계정 정보 관련 서비스
 * - 로그인한 사용자 정보 조회
 * - 개인/기업/관리자 공통 대응
 */
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AdminRepository adminRepository;
    private final ProfileRepository profileRepository;

    /**
     * 로그인된 사용자 정보 조회
     *
     * @param user 인증된 사용자 정보 (JWT 기반)
     * @return PersonalInfoDTO (공통 유저 정보)
     */
    public PersonalInfoDTO getMyInfo(CustomUserPrincipal user) {
        if (user == null) {
            throw new NotFoundException("로그인 정보가 없습니다.");
        }

        Long accountId = user.getAccountId();
        Long profileId = user.getProfileId(); // 기업/관리자는 null 가능
        Role role = user.getRole();

        // 필수 정보 누락 시 예외 처리
        if (accountId == null || role == null) {
            throw new NotFoundException(USER_NOT_FOUND.getMessage());
        }

        String name;
        String profileImageUrl = null;

        if (role == Role.ADMIN || role == Role.SUPER) {
            // 관리자 계정 조회
            Admin admin = adminRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getMessage()));
            name = admin.getName();
        } else {
            // 일반 사용자(Account) 조회
            Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getMessage()));
            name = account.getName();

            // 개인회원인 경우 profile 정보 조회
            if (profileId != null) {
                Profile profile = profileRepository.findById(profileId)
                    .orElseThrow(() -> new NotFoundException(PROFILE_NOT_FOUND.getMessage()));
                profileImageUrl = profile.getProfileImageKey();
            }
        }

        return new PersonalInfoDTO(
            accountId,
            profileId,
            role,
            name,
            profileImageUrl
        );
    }
}
