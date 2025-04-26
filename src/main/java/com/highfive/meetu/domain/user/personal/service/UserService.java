package com.highfive.meetu.domain.user.personal.service;

import com.highfive.meetu.domain.auth.personal.dto.UserInfoDTO;
import com.highfive.meetu.domain.user.common.entity.Account;
import com.highfive.meetu.domain.user.common.entity.Profile;
import com.highfive.meetu.domain.user.common.repository.AccountRepository;
import com.highfive.meetu.domain.user.common.repository.ProfileRepository;
import com.highfive.meetu.global.common.exception.NotFoundException;
import com.highfive.meetu.global.common.response.ResultData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final AccountRepository accountRepository;
    private final ProfileRepository profileRepository;

    @Transactional(readOnly = true)
    public ResultData<UserInfoDTO> getMyInfo(Long userId) {
        Account account = accountRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("사용자 정보를 찾을 수 없습니다."));

        Profile profile = profileRepository.findByAccountId(userId).orElse(null);
        return ResultData.success(1, UserInfoDTO.from(account, profile));
    }
}
