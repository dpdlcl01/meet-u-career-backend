package com.highfive.meetu.domain.resume.business.service;

import com.highfive.meetu.domain.resume.business.dto.TalentDto;
import com.highfive.meetu.domain.resume.common.entity.Resume;
import com.highfive.meetu.domain.resume.common.repository.TalentRepository;
import com.highfive.meetu.domain.user.common.entity.Profile;
import com.highfive.meetu.domain.user.common.entity.Account;
import com.highfive.meetu.domain.job.common.entity.JobCategory;
import com.highfive.meetu.domain.job.common.entity.Location;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * 인재(대표 이력서) 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class TalentService {
    private final TalentRepository talentRepository;


    /*
        If 경력 null >> show 신입

        when company clicks on resume. add view count.
     */



    /**
     * 대표 이력서를 조회하여 응답 DTO 리스트를 반환
     * 
     * @return 인재 응답 DTO 리스트
     */
    public List<TalentDto> findAllPrimaryResumes() {
        return talentRepository.findByIsPrimaryTrue().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Resume 엔티티를 TalentDto로 변환
     * 
     * @param r 이력서 엔티티
     * @return 변환된 TalentDto
     */
    private TalentDto toDto(Resume r) {
        Profile p = r.getProfile();
        Account a = p.getAccount();
        JobCategory job = p.getDesiredJobCategory();
        Location loc = p.getDesiredLocation();

        String experience = "경력 " + p.getExperienceLevel() + "년";
        String education = "학력 " + mapEducationLevel(p.getEducationLevel());

        List<String> skills = (p.getSkills() != null && !p.getSkills().isEmpty())
                ? Arrays.asList(p.getSkills().split(","))
                : Collections.emptyList();
        int moreSkills = Math.max(0, skills.size() - 5);

        return TalentDto.builder()
                .id(r.getId())
                .name(a.getName())
                .title(job != null ? job.getJobName() : "")
                .location(loc != null ? loc.getFullLocation() : "")
                .experience(experience)
                .education(education)
                .skills(skills.stream().limit(5).collect(Collectors.toList()))
                // 프로필 이미지 키 설정 (null일 경우 빈 문자열)
                .profileImageKey(p.getProfileImageKey() != null ? p.getProfileImageKey() : "")
                .moreSkills(moreSkills)
                .description(r.getOverview())
                .build();
    }

    /**
     * 학력 레벨 코드를 한글 문자열로 변환
     * 
     * @param code 학력 레벨 코드
     * @return 학력 문자열
     */
    private String mapEducationLevel(Integer code) {
        if (code == null) {
            return "학력무관";
        }
        switch (code) {
            case Profile.EducationLevel.HIGH_SCHOOL:
                return "고졸";
            case Profile.EducationLevel.COLLEGE:
                return "대졸";
            case Profile.EducationLevel.UNIVERSITY:
                return "대졸";
            case Profile.EducationLevel.MASTER:
                return "석사";
            case Profile.EducationLevel.DOCTOR:
                return "박사";
            default:
                return "학력무관";
        }
    }
}
