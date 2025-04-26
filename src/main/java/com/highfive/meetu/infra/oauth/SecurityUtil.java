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

/**
 * Spring Security 기반 인증 정보 접근 유틸리티
 * - 현재 로그인한 사용자의 accountId, profileId, companyId, adminId, role 조회 지원
 */
@Component
@RequiredArgsConstructor
public class SecurityUtil {

    private final AccountRepository accountRepository;
    private final AdminRepository adminRepository;

    /**
     * 현재 사용자가 인증되었는지 여부 확인
     * - 로그인 상태인지 비로그인 상태인지 판단
     *
     * @return true: 인증됨, false: 비인증 상태(익명 사용자)
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal());
    }

    /**
     * 현재 로그인한 사용자의 accountId 가져오기
     *
     * @return accountId
     * @throws NotFoundException 인증 정보가 없는 경우
     */
    public static Long getAccountId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new NotFoundException("인증 정보가 없습니다. (1)");
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof Long accountId) {
            return accountId;
        }
        if (principal instanceof String principalStr) {
            try {
                return Long.parseLong(principalStr);
            } catch (NumberFormatException e) {
                throw new NotFoundException("잘못된 인증 정보입니다.");
            }
        }
        if (principal instanceof CustomUserPrincipal customUserPrincipal) {
            return customUserPrincipal.getAccountId();
        }
        throw new NotFoundException("인증 정보가 없습니다. (2)");
    }

    /**
     * 현재 로그인한 사용자의 profileId 가져오기
     * - 개인회원(PERSONAL)만 존재 (기업회원, 관리자 없음)
     *
     * @return profileId
     * @throws NotFoundException 인증 정보가 없거나 profileId가 없는 경우
     */
    public static Long getProfileId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserPrincipal)) {
            throw new NotFoundException("인증 정보가 없습니다. (3)");
        }

        CustomUserPrincipal principal = (CustomUserPrincipal) auth.getPrincipal();
        if (principal.getProfileId() == null) {
            throw new NotFoundException("profileId가 존재하지 않습니다. (개인회원이 아닐 수 있습니다.)");
        }

        return principal.getProfileId();
    }

    /**
     * 현재 로그인한 사용자의 companyId 가져오기
     * - 기업회원(BUSINESS)만 존재 (개인회원, 관리자 없음)
     *
     * @param accountRepository AccountRepository (DB 조회용)
     * @return companyId
     * @throws NotFoundException 인증 정보가 없거나 companyId가 없는 경우
     */
    public static Long getCompanyId(AccountRepository accountRepository) {
        Long accountId = getAccountId();
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("계정을 찾을 수 없습니다."));

        if (account.getAccountType() != Account.AccountType.BUSINESS) {
            throw new NotFoundException("기업 회원이 아닙니다.");
        }

        if (account.getCompany() == null) {
            throw new NotFoundException("소속 회사 정보(company)가 없습니다.");
        }

        return account.getCompany().getId();
    }

    /**
     * 현재 로그인한 관리자의 adminId 가져오기
     * - 관리자(SUPER, ADMIN)만 호출 가능
     *
     * @return adminId
     * @throws NotFoundException 인증 정보가 없거나 관리자 권한이 아닌 경우
     */
    public static Long getAdminId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof CustomUserPrincipal)) {
            throw new NotFoundException("인증 정보가 없습니다.");
        }

        CustomUserPrincipal principal = (CustomUserPrincipal) auth.getPrincipal();

        if (principal.getRole() != Role.ADMIN && principal.getRole() != Role.SUPER) {
            throw new NotFoundException("관리자 권한이 아닙니다.");
        }

        return principal.getAccountId(); // 관리자 토큰의 경우 이 값이 adminId 역할을 함
    }

    /**
     * 현재 로그인한 사용자의 Role 가져오기
     * - PERSONAL, BUSINESS는 account 테이블 조회
     * - ADMIN, SUPER는 admin 테이블 조회
     */
    public Role getUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserPrincipal)) {
            throw new NotFoundException("인증 정보가 없습니다.");
        }

        CustomUserPrincipal principal = (CustomUserPrincipal) auth.getPrincipal();
        Role role = principal.getRole();

        if (role == Role.PERSONAL || role == Role.BUSINESS) {
            // accountId로 account 테이블 조회
            Account account = accountRepository.findById(principal.getAccountId())
                    .orElseThrow(() -> new NotFoundException("사용자 정보를 찾을 수 없습니다."));

            if (account.getAccountType() == Account.AccountType.PERSONAL) {
                return Role.PERSONAL;
            } else if (account.getAccountType() == Account.AccountType.BUSINESS) {
                return Role.BUSINESS;
            } else {
                throw new IllegalStateException("알 수 없는 회원 유형입니다.");
            }
        } else if (role == Role.ADMIN || role == Role.SUPER) {
            // 관리자면 admin 테이블 조회
            Admin admin = adminRepository.findById(principal.getAccountId())
                    .orElseThrow(() -> new NotFoundException("관리자 계정을 찾을 수 없습니다."));

            if (admin.getRole() == Admin.Role.SUPER) {
                return Role.SUPER;
            } else if (admin.getRole() == Admin.Role.ADMIN) {
                return Role.ADMIN;
            } else {
                throw new IllegalStateException("알 수 없는 관리자 권한입니다.");
            }
        } else {
            throw new IllegalStateException("알 수 없는 사용자 역할입니다.");
        }
    }

}
