package com.highfive.meetu.infra.oauth;

import com.highfive.meetu.domain.auth.personal.type.Role;
import com.highfive.meetu.domain.user.common.entity.Account;
import com.highfive.meetu.domain.user.common.entity.Admin;
import com.highfive.meetu.domain.user.common.repository.AccountRepository;
import com.highfive.meetu.domain.user.common.repository.AdminRepository;
import com.highfive.meetu.global.common.exception.NotFoundException;
import com.highfive.meetu.global.security.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

    private final AccountRepository accountRepository;
    private final AdminRepository adminRepository;

    /**
     * 현재 로그인한 사용자의 accountId 가져오기
     */
    public static Long getAccountId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Long) Optional.ofNullable(auth)
            .map(Authentication::getPrincipal)
            .filter(principal -> principal instanceof Long)
            .map(Long.class::cast)
            .orElseThrow(() -> new NotFoundException("인증 정보가 없습니다."));
    }

    /**
     * 현재 로그인한 사용자의 profileId 가져오기 (개인회원만 존재)
     */
    public static Long getProfileId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserPrincipal)) {
            throw new NotFoundException("인증 정보가 없습니다.");
        }

        CustomUserPrincipal principal = (CustomUserPrincipal) auth.getPrincipal();
        if (principal.getProfileId() == null) {
            throw new NotFoundException("profileId가 존재하지 않습니다. (개인회원이 아닐 수 있습니다.)");
        }

        return principal.getProfileId();
    }

    /**
     * accountId를 기준으로 회원 유형(PERSONAL, BUSINESS, SUPER, ADMIN) Role 조회
     */
    public Role getUserRole(Long accountId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new NotFoundException("사용자 정보를 찾을 수 없습니다."));

        if (account.getAccountType() == Account.AccountType.PERSONAL) {
            return Role.PERSONAL;
        } else if (account.getAccountType() == Account.AccountType.BUSINESS) {
            return Role.BUSINESS;
        } else {
            // 관리자 권한 처리
            Admin admin = adminRepository.findByEmail(account.getEmail())
                .orElseThrow(() -> new NotFoundException("관리자 계정을 찾을 수 없습니다."));

            if (admin.getRole() == Admin.Role.SUPER) {
                return Role.SUPER;
            } else if (admin.getRole() == Admin.Role.ADMIN) {
                return Role.ADMIN;
            } else {
                throw new IllegalStateException("알 수 없는 관리자 권한입니다.");
            }
        }
    }
}
