package com.highfive.meetu.domain.company.personal.service;

import com.highfive.meetu.domain.company.common.entity.Company;
import com.highfive.meetu.domain.company.common.entity.CompanyFollow;
import com.highfive.meetu.domain.company.common.repository.CompanyFollowRepository;
import com.highfive.meetu.domain.company.common.repository.CompanyRepository;
import com.highfive.meetu.domain.user.common.entity.Profile;
import com.highfive.meetu.domain.user.common.repository.ProfileRepository;
import com.highfive.meetu.global.common.exception.NotFoundException;
import com.highfive.meetu.infra.oauth.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompanyFollowService {

    private final CompanyFollowRepository companyFollowRepository;
    private final ProfileRepository profileRepository;
    private final CompanyRepository companyRepository;

    @Transactional
    public void follow(Long companyId) {
        Long profileId = SecurityUtil.getProfileId();
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new NotFoundException("기업 정보를 찾을 수 없습니다."));
        Profile profile = profileRepository.findById(profileId)
            .orElseThrow(() -> new NotFoundException("프로필 정보를 찾을 수 없습니다."));

        if (companyFollowRepository.existsByProfileAndCompany(profile, company)) {
            throw new IllegalStateException("이미 등록된 관심 기업입니다.");
        }

        CompanyFollow follow = CompanyFollow.builder()
            .profile(profile)
            .company(company)
            .build();

        companyFollowRepository.save(follow);
    }

    @Transactional
    public void unfollow(Long companyId) {
        Long profileId = SecurityUtil.getProfileId();
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new NotFoundException("기업 정보를 찾을 수 없습니다."));
        Profile profile = profileRepository.findById(profileId)
            .orElseThrow(() -> new NotFoundException("프로필 정보를 찾을 수 없습니다."));

        CompanyFollow follow = companyFollowRepository.findByProfileAndCompany(profile, company)
            .orElseThrow(() -> new IllegalStateException("등록된 관심 기업이 없습니다."));

        companyFollowRepository.delete(follow);
    }
}
