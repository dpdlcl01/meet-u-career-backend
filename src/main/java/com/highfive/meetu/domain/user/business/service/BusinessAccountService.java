package com.highfive.meetu.domain.user.business.service;

import com.highfive.meetu.domain.company.common.entity.Company;
import com.highfive.meetu.domain.company.common.repository.CompanyRepository;
import com.highfive.meetu.domain.user.business.dto.BusinessSignUpRequestDTO;
import com.highfive.meetu.domain.user.common.entity.Account;
import com.highfive.meetu.domain.user.common.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BusinessAccountService {
    private final AccountRepository accountRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    // 이메일(아이디) 중복 체크
    public boolean findByEmail(String email) {
        return accountRepository.existsByEmail(email);
    }

    // 기업회원 회원가입
    public BusinessSignUpRequestDTO save(BusinessSignUpRequestDTO dto) {
        // Company
        Company company = companyRepository.save(dto.toCompany());

        // Account
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        Account account = accountRepository.save(dto.toAccount(encodedPassword, company));

        return com.highfive.meetu.domain.user.business.dto.BusinessSignUpRequestDTO.from(account, company);
    }
}
