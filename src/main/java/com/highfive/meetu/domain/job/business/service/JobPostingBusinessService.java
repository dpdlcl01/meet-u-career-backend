package com.highfive.meetu.domain.job.business.service;

import com.highfive.meetu.domain.company.common.entity.Company;
import com.highfive.meetu.domain.job.business.dto.JobPostingResponseDTO;
import com.highfive.meetu.domain.job.common.repository.JobPostingRepository;
import com.highfive.meetu.domain.user.common.entity.Account;
import com.highfive.meetu.domain.user.common.repository.AccountRepository;
import com.highfive.meetu.global.common.exception.NotFoundException;
import com.highfive.meetu.infra.oauth.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobPostingBusinessService {
    private final AccountRepository accountRepository;
    private final JobPostingRepository jobPostingRepository;

    public List<JobPostingResponseDTO> getMyCompanyJobPostings() {
        Long accountId = SecurityUtil.getAccountId();

        // 1. accountId로 Account 조회
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new NotFoundException("계정을 찾을 수 없습니다."));

        // 2. Account에 연결된 Company 가져오기
        Company company = account.getCompany();
        if (company == null) {
            throw new NotFoundException("소속된 기업이 없습니다.");
        }

        // 3. Company 기준으로 공고 조회
        return jobPostingRepository.findAllByCompanyId(company.getId())
            .stream()
            .map(JobPostingResponseDTO::fromEntity)
            .toList();
    }
}