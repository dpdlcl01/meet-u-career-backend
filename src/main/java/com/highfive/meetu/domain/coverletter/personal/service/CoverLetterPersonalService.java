package com.highfive.meetu.domain.coverletter.personal.service;

import com.highfive.meetu.domain.coverletter.common.entity.CoverLetter;
import com.highfive.meetu.domain.coverletter.common.entity.CoverLetterContent;
import com.highfive.meetu.domain.coverletter.common.repository.CoverLetterContentRepository;
import com.highfive.meetu.domain.coverletter.common.repository.CoverLetterRepository;
import com.highfive.meetu.domain.coverletter.personal.dto.CoverLetterPersonalDTO;
import com.highfive.meetu.domain.coverletter.personal.dto.CoverLetterPersonalViewDTO;
import com.highfive.meetu.domain.user.common.entity.Profile;
import com.highfive.meetu.domain.user.common.repository.ProfileRepository;
import com.highfive.meetu.global.common.exception.BadRequestException;
import com.highfive.meetu.global.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoverLetterPersonalService {

    private final ProfileRepository profileRepository;
    private final CoverLetterRepository coverLetterRepository;
    private final CoverLetterContentRepository coverLetterContentRepository;

    // 자기소개서 목록
    public List<CoverLetterPersonalDTO> getCoverLetterList(Long profileId) {
        List<CoverLetter> entities = coverLetterRepository.findByProfileIdAndStatusOrderByUpdatedAtDesc(
                profileId, CoverLetter.Status.ACTIVE
        );

        return entities.stream()
                .map(CoverLetterPersonalDTO::fromEntity)
                .toList();
    }

    // 자기소개서 상세
    @Transactional(readOnly = true)
    public CoverLetterPersonalViewDTO getCoverLetterDetail(Long coverLetterId) {
        CoverLetter coverLetter = coverLetterRepository.findById(coverLetterId)
                .orElseThrow(() -> new NotFoundException("자기소개서를 찾을 수 없습니다."));

        return CoverLetterPersonalViewDTO.fromEntity(coverLetter);
    }

    // 자기소개서 수정
    @Transactional
    public void updateCoverLetter(Long coverLetterId, CoverLetterPersonalDTO dto) {

        System.out.println("===== [updateCoverLetter] dto: " + dto);
        for (CoverLetterPersonalDTO.CoverLetterContentDTO contentDto : dto.getContents()) {
            System.out.println("===== [updateCoverLetter] contentDto: id=" + contentDto.getId()
                    + ", sectionTitle=" + contentDto.getSectionTitle()
                    + ", content=" + contentDto.getContent());
        }
        CoverLetter coverLetter = coverLetterRepository.findById(coverLetterId)
                .orElseThrow(() -> new NotFoundException("자기소개서를 찾을 수 없습니다."));

        // 제목 수정
        coverLetter.setTitle(dto.getTitle());

        for (CoverLetterPersonalDTO.CoverLetterContentDTO contentDto : dto.getContents()) {
            if (contentDto.getId() != null) {
                // 기존 항목 수정
                CoverLetterContent content = coverLetterContentRepository.findById(contentDto.getId())
                        .orElseThrow(() -> new NotFoundException("자기소개서 항목을 찾을 수 없습니다."));

                content.setSectionTitle(contentDto.getSectionTitle());
                content.setContent(contentDto.getContent());
                content.setContentOrder(contentDto.getContentOrder());
            } else {
                // 새 항목 추가
                CoverLetterContent newContent = CoverLetterContent.builder()
                        .coverLetter(coverLetter)
                        .sectionTitle(contentDto.getSectionTitle())
                        .content(contentDto.getContent())
                        .contentOrder(contentDto.getContentOrder())
                        .build();
                coverLetterContentRepository.save(newContent);
            }
        }
    }




    // 자기소개서 삭제
    @Transactional
    public void deleteCoverLetter(Long coverLetterId) {
        CoverLetter coverLetter = coverLetterRepository.findById(coverLetterId)
                .orElseThrow(() -> new NotFoundException("자기소개서를 찾을 수 없습니다."));

        coverLetter.setStatus(CoverLetter.Status.DELETED);
    }


    /**
     * 자기소개서 저장 메서드
     */
    @Transactional
    public void saveCoverLetter(Long profileId, CoverLetterPersonalDTO dto) {
        // 1. 프로필 조회 (로그인 사용자 기준)
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new NotFoundException("프로필을 찾을 수 없습니다."));

        // 2. CoverLetter 저장
        CoverLetter coverLetter = CoverLetter.builder()
                .profile(profile)
                .title(dto.getTitle())
                .status(CoverLetter.Status.ACTIVE)
                .build();

        coverLetter = coverLetterRepository.save(coverLetter); // save 후 영속성 부여

        // 3. CoverLetterContent 저장
        CoverLetter finalCoverLetter = coverLetter;
        List<CoverLetterContent> contentList = dto.getContents().stream()
                .map(contentDto -> contentDto.toEntity(finalCoverLetter))
                .toList();

        coverLetterContentRepository.saveAll(contentList);
    }


}
