package com.highfive.meetu.domain.user.personal.controller;

import com.highfive.meetu.domain.auth.personal.type.Role;
import com.highfive.meetu.domain.user.common.entity.Account;
import com.highfive.meetu.domain.user.common.entity.Profile;
import com.highfive.meetu.domain.user.common.repository.AccountRepository;
import com.highfive.meetu.domain.user.common.repository.ProfileRepository;
import com.highfive.meetu.domain.user.personal.dto.PersonalInfoDTO;
import com.highfive.meetu.global.common.exception.NotFoundException;
import com.highfive.meetu.global.common.response.ResultData;
import com.highfive.meetu.global.security.LoginUser;
import com.highfive.meetu.global.security.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.highfive.meetu.global.common.response.ErrorCode.*;

/**
 * 사용자(개인회원) API
 */
@RestController
@RequestMapping("/api/personal")
@RequiredArgsConstructor
public class PersonalController {
    private final AccountRepository accountRepository;
    private final ProfileRepository profileRepository;

    @PreAuthorize("hasRole('USER')") // 개인회원만 접근 허용
    @GetMapping("/me")
    public ResultData<PersonalInfoDTO> myPage(@LoginUser CustomUserPrincipal user) {
        if (user == null) {
            return ResultData.fail("로그인 정보가 없습니다.");
        }

        Long accountId = user.getAccountId();
        Long profileId = user.getProfileId();
        Role role = user.getRole();

        if (accountId == null || role == null) {
            return ResultData.fail(USER_NOT_FOUND);
        }

        Account account = accountRepository.findById(accountId)
            .orElse(null);

        if (account == null) {
            return ResultData.fail(USER_NOT_FOUND);
        }

        Profile profile = profileRepository.findById(profileId)
            .orElse(null);

        if (profile == null) {
            return ResultData.fail(PROFILE_NOT_FOUND);
        }

        String name = account.getName();
        String profileImageKey = profile.getProfileImageKey();

        PersonalInfoDTO dto = new PersonalInfoDTO(
            accountId,
            profileId,
            role,
            name,
            profileImageKey
        );

        return ResultData.success(1, dto);
    }
}
