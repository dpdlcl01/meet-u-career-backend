package com.highfive.meetu.domain.resume.personal.service;

import com.highfive.meetu.domain.coverletter.common.entity.CoverLetter;
import com.highfive.meetu.domain.coverletter.common.repository.CoverLetterRepository;
import com.highfive.meetu.domain.job.common.entity.JobCategory;
import com.highfive.meetu.domain.job.common.entity.Location;
import com.highfive.meetu.domain.job.common.repository.JobCategoryRepository;
import com.highfive.meetu.domain.job.common.repository.LocationRepository;
import com.highfive.meetu.domain.resume.common.entity.Resume;
import com.highfive.meetu.domain.resume.common.entity.ResumeContent;
import com.highfive.meetu.domain.resume.common.repository.ResumeContentRepository;
import com.highfive.meetu.domain.resume.common.repository.ResumeRepository;
import com.highfive.meetu.domain.resume.common.repository.ResumeViewLogRepository;
import com.highfive.meetu.domain.resume.personal.dto.*;
import com.highfive.meetu.domain.user.common.entity.Profile;
import com.highfive.meetu.domain.user.common.repository.ProfileRepository;
import com.highfive.meetu.global.common.exception.BadRequestException;
import com.highfive.meetu.global.common.exception.NotFoundException;
import com.highfive.meetu.infra.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResumePersonalService {

    private final ResumeRepository resumeRepository;
    private final ProfileRepository profileRepository;
    private final CoverLetterRepository coverLetterRepository;
    private final ResumeContentRepository resumeContentRepository;
    private final S3Service s3Service;
    private final LocationRepository locationRepository;
    private final JobCategoryRepository jobCategoryRepository;
    private final ResumeViewLogRepository resumeViewLogRepository;


    // 이력서 초기 생성 메서드 - "이력서 작성" 버튼을 눌러서 이력서 작성 페이지로 넘어갈 때 데이터 생성
    @Transactional
    public Long initResume(Long profileId, Integer resumeType) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new NotFoundException("프로필을 찾을 수 없습니다."));

        // 기본 제목 설정
        String defaultTitle = "새 이력서";

        Resume resume = Resume.builder()
                .title(defaultTitle)
                .resumeType(resumeType)
                .profile(profile)
                .status(Resume.Status.DRAFT)     // 0 = 임시저장
                .isPrimary(false)                 // 기본은 대표이력서 아님
                .build();

        resumeRepository.save(resume);
        return resume.getId();
    }

    // 파일 이력서 저장
    @Transactional
    public Long saveFileResume(ResumeFileDTO dto, MultipartFile file) {
        Profile profile = profileRepository.findById(dto.getProfileId())
                .orElseThrow(() -> new NotFoundException("프로필을 찾을 수 없습니다."));

        String fileKey = s3Service.uploadFile(file, "resume");
        String fileName = file.getOriginalFilename();
        String fileType = file.getContentType();

        Resume resume = Resume.builder()
                .profile(profile)
                .title(dto.getTitle())
                .resumeType(Resume.ResumeType.FILE_UPLOAD) // 1
                .resumeFileKey(fileKey)
                .resumeFileName(fileName)
                .resumeFileType(fileType)
                .isPrimary(false)
                .status(dto.getStatus() != null ? dto.getStatus() : Resume.Status.PRIVATE)
                .build();

        resumeRepository.save(resume);
        return resume.getId();
    }


    // 기존 파일 이력서 수정 또는 init 이후 호출시
    @Transactional
    public void updateFileResume(Long resumeId, MultipartFile resumeFile) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new NotFoundException("이력서를 찾을 수 없습니다."));

        // 파일 필수 체크
        if (resumeFile == null || resumeFile.isEmpty()) {
            throw new BadRequestException("이력서 파일이 비어있습니다.");
        }

        // S3 업로드
        String resumeFileKey = s3Service.uploadFile(resumeFile, "resume");
        resume.setResumeFileKey(resumeFileKey);
        resume.setResumeFileName(resumeFile.getOriginalFilename());
        resume.setResumeFileType(resumeFile.getContentType());

        // 상태 업데이트: 공개 or 비공개는 프론트에서 설정하거나 기본값 유지
        resumeRepository.save(resume);
    }


    // url 이력서 생성
    @Transactional
    public Long saveUrlResume(ResumeUrlDTO dto) {
        // 1. 필수 값 검증
        if (dto.getProfileId() == null || dto.getTitle() == null || dto.getResumeUrl() == null) {
            throw new BadRequestException("필수 항목이 누락되었습니다.");
        }

        // 2. 프로필 조회
        Profile profile = profileRepository.findById(dto.getProfileId())
                .orElseThrow(() -> new NotFoundException("프로필을 찾을 수 없습니다."));

        // 3. 이력서 엔티티 생성
        Resume resume = Resume.builder()
                .profile(profile)
                .title(dto.getTitle())
                .resumeType(Resume.ResumeType.URL)
                .resumeUrl(dto.getResumeUrl())
                .overview("") // URL 이력서는 overview 없이 저장
                .isPrimary(false)
                .status(Resume.Status.PRIVATE)
                .build();

        // 4. 저장
        resumeRepository.save(resume);

        return resume.getId();
    }





    // 이력서 기본 정보 저장
    @Transactional
    public void updateResumeBasicInfo(Long resumeId, ResumeBasicInfoDTO dto, MultipartFile resumeFile, MultipartFile profileImage) {

        // 1. Resume 조회
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new NotFoundException("이력서를 찾을 수 없습니다."));

        // 2. Profile 조회
        Profile profile = profileRepository.findById(dto.getProfileId())
                .orElseThrow(() -> new NotFoundException("프로필을 찾을 수 없습니다."));

        // 3. 이력서 관련 필드 업데이트
        resume.setTitle(dto.getTitle());
        resume.setOverview(dto.getOverview());
        resume.setResumeType(dto.getResumeType() != null ? dto.getResumeType() : 0);
        resume.setResumeUrl(dto.getResumeUrl());
        resume.setExtraLink1(dto.getExtraLink1());
        resume.setExtraLink2(dto.getExtraLink2());
        resume.setStatus(dto.getStatus());
        resume.setIsPrimary(dto.getIsPrimary() != null ? dto.getIsPrimary() : false);

        if (resumeFile != null && !resumeFile.isEmpty()) {
            String resumeFileKey = s3Service.uploadFile(resumeFile, "resume");
            resume.setResumeFileKey(resumeFileKey);
            resume.setResumeFileName(resumeFile.getOriginalFilename());
            resume.setResumeFileType(resumeFile.getContentType());
        }

        // 4. 프로필 관련 필드 업데이트
        profile.setExperienceLevel(dto.getExperienceLevel());
        profile.setEducationLevel(dto.getEducationLevel());
        profile.setDesiredSalaryCode(dto.getDesiredSalaryCode());
        profile.setSkills(dto.getSkills());

        if (dto.getLocationId() != null) {
            Location location = locationRepository.findById(dto.getLocationId())
                    .orElseThrow(() -> new NotFoundException("거주 지역을 찾을 수 없습니다."));
            profile.setLocation(location);
        }

        if (dto.getDesiredJobCategoryId() != null) {
            JobCategory jobCategory = jobCategoryRepository.findById(dto.getDesiredJobCategoryId())
                    .orElseThrow(() -> new NotFoundException("희망 직무를 찾을 수 없습니다."));
            profile.setDesiredJobCategory(jobCategory);
        }

        if (profileImage != null && !profileImage.isEmpty()) {
            String profileImageKey = s3Service.uploadFile(profileImage, "profile");
            profile.setProfileImageKey(profileImageKey);
        }

        // 저장은 변경 감지로 처리됨
    }


    // 항목 추가 메서드
    @Transactional
    public Long saveResumeContent(ResumeContentDTO dto, MultipartFile file) {

        // 1. 이력서 존재 여부 확인
        Resume resume = resumeRepository.findById(dto.getResumeId())
                .orElseThrow(() -> new NotFoundException("이력서를 찾을 수 없습니다."));

        // 2. 파일 업로드 처리 (선택)
        String contentFileKey = null;
        String contentFileName = null;
        String contentFileType = null;

        if (file != null && !file.isEmpty()) {
            contentFileKey = s3Service.uploadFile(file, "resume-content");
            contentFileName = file.getOriginalFilename();
            contentFileType = file.getContentType();
        }

        // 3. sectionType 필수 값 체크 (0~3 또는 4~6)
        if (dto.getSectionType() == null) {
            throw new BadRequestException("항목 유형(sectionType)은 필수입니다.");
        }

        // 4. ResumeContent 생성
        ResumeContent content = ResumeContent.builder()
                .resume(resume)
                .sectionType(dto.getSectionType())
                .sectionTitle(dto.getSectionTitle())
                .organization(dto.getOrganization())
                .title(dto.getTitle())
                .field(dto.getField())
                .dateFrom(dto.getDateFrom())
                .dateTo(dto.getDateTo())
                .description(dto.getDescription())
                .contentOrder(dto.getContentOrder() != null ? dto.getContentOrder() : 0)
                .contentFileKey(contentFileKey)
                .contentFileName(contentFileName)
                .contentFileType(contentFileType)
                .build();

        // 5. 저장
        resumeContentRepository.save(content);

        return content.getId();
    }


    // 항목 수정 메서드
    @Transactional
    public Long updateResumeContent(ResumeContentDTO dto, MultipartFile file) {

        ResumeContent content = resumeContentRepository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("이력서 항목을 찾을 수 없습니다."));

        // 파일이 전달된 경우 → S3 업로드 후 교체
        if (file != null && !file.isEmpty()) {
            String newKey = s3Service.uploadFile(file, "resume-content");
            content.setContentFileKey(newKey);
            content.setContentFileName(file.getOriginalFilename());
            content.setContentFileType(file.getContentType());
        }

        // 항목 정보 수정
        content.setSectionType(dto.getSectionType());
        content.setSectionTitle(dto.getSectionTitle());
        content.setOrganization(dto.getOrganization());
        content.setTitle(dto.getTitle());
        content.setField(dto.getField());
        content.setDescription(dto.getDescription());
        content.setDateFrom(dto.getDateFrom());
        content.setDateTo(dto.getDateTo());
        content.setContentOrder(dto.getContentOrder());

        return content.getId();
    }


    // 항목 삭제 메서드
    @Transactional
    public void deleteResumeContent(Long resumeId, Long contentId) {
        ResumeContent content = resumeContentRepository.findById(contentId)
                .orElseThrow(() -> new NotFoundException("이력서 항목을 찾을 수 없습니다."));

        if (!content.getResume().getId().equals(resumeId)) {
            throw new BadRequestException("해당 이력서에 속한 항목이 아닙니다.");
        }

        resumeContentRepository.delete(content);
    }


    // 전체 이력서 저장
    @Transactional
    public void saveAllAtOnce(Long resumeId, ResumeWriteRequestDTO dto) {

        // 🔹 1. 이력서 조회 (필수)
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new NotFoundException("이력서를 찾을 수 없습니다."));

        // 🔹 2. 프로필 조회 및 수정
        Profile profile = profileRepository.findById(dto.getProfile().getId())
                .orElseThrow(() -> new NotFoundException("프로필을 찾을 수 없습니다."));

        // S3 업로드: 프로필 이미지
        if (dto.getProfileImage() != null && !dto.getProfileImage().isEmpty()) {
            String profileImageKey = s3Service.uploadFile(dto.getProfileImage(), "profile");
            profile.setProfileImageKey(profileImageKey);
        }

        Location location = null;
        if (dto.getProfile().getLocationId() != null) {
            location = locationRepository.findById(dto.getProfile().getLocationId())
                    .orElseThrow(() -> new NotFoundException("선택한 지역 정보를 찾을 수 없습니다."));
        }
        profile.setLocation(location);

        JobCategory desiredJob = null;
        if (dto.getProfile().getDesiredJobCategoryId() != null) {
            desiredJob = jobCategoryRepository.findById(dto.getProfile().getDesiredJobCategoryId())
                    .orElseThrow(() -> new NotFoundException("희망 직무 정보를 찾을 수 없습니다."));
        }
        profile.setDesiredJobCategory(desiredJob);

        profile.setSkills(dto.getProfile().getSkills());
        profile.setEducationLevel(dto.getProfile().getEducationLevel());
        profile.setExperienceLevel(dto.getProfile().getExperienceLevel());
        profileRepository.save(profile);

        // 🔹 3. 이력서 기본 정보 수정 + 파일 처리
        if (dto.getResumeFile() != null && !dto.getResumeFile().isEmpty()) {
            String resumeFileKey = s3Service.uploadFile(dto.getResumeFile(), "resume");
            resume.setResumeFileKey(resumeFileKey);
            resume.setResumeFileName(dto.getResumeFile().getOriginalFilename());
            resume.setResumeFileType(dto.getResumeFile().getContentType());
        }

        resume.setTitle(dto.getResume().getTitle());
        resume.setOverview(dto.getResume().getOverview());
        resume.setResumeType(dto.getResume().getResumeType());
        resume.setResumeUrl(dto.getResume().getResumeUrl());
        resume.setExtraLink1(dto.getResume().getExtraLink1());
        resume.setExtraLink2(dto.getResume().getExtraLink2());
        resume.setStatus(dto.getResume().getStatus() != null ? dto.getResume().getStatus() : Resume.Status.PRIVATE);
        resumeRepository.save(resume);

        // 🔹 4. 기존 항목 모두 삭제
        resumeContentRepository.deleteAllByResumeId(resumeId);

        // 🔹 5. 항목 전체 새로 저장
        if (dto.getResumeContents() != null && !dto.getResumeContents().isEmpty()) {
            List<ResumeContent> contents = new ArrayList<>();

            for (int i = 0; i < dto.getResumeContents().size(); i++) {
                ResumeContentDTO contentDto = dto.getResumeContents().get(i);

                MultipartFile file = (dto.getContentFiles() != null && dto.getContentFiles().size() > i)
                        ? dto.getContentFiles().get(i) : null;

                String contentFileKey = null;
                String contentFileName = null;
                String contentFileType = null;

                if (file != null && !file.isEmpty()) {
                    contentFileKey = s3Service.uploadFile(file, "resume-content");
                    contentFileName = file.getOriginalFilename();
                    contentFileType = file.getContentType();
                }

                ResumeContent content = ResumeContent.builder()
                        .resume(resume)
                        .sectionType(contentDto.getSectionType())
                        .sectionTitle(contentDto.getSectionTitle())
                        .organization(contentDto.getOrganization())
                        .title(contentDto.getTitle())
                        .field(contentDto.getField())
                        .dateFrom(contentDto.getDateFrom())
                        .dateTo(contentDto.getDateTo())
                        .description(contentDto.getDescription())
                        .contentOrder(contentDto.getContentOrder() != null ? contentDto.getContentOrder() : i)
                        .contentFileKey(contentFileKey)
                        .contentFileName(contentFileName)
                        .contentFileType(contentFileType)
                        .build();

                contents.add(content);
            }

            resumeContentRepository.saveAll(contents);
        }
    }




    /**
     * 주어진 프로필 ID에 해당하는 이력서 목록을 상태(status = 0) 기준으로 최신순 정렬하여 조회합니다.
     * 상태 0은 '활성 상태'의 이력서만 의미합니다.
     *
     * @param profileId 프로필 ID (개인 회원 기준)
     * @return 이력서 목록 DTO 리스트
     */
    public List<ResumePersonalDTO> getResumeListByProfileId(Long profileId) {

        // 상태가 '활성(0)'인 이력서들을 profileId 기준으로 최신순(updatedAt 기준 내림차순)으로 조회
        List<Resume> resumeList = resumeRepository.findAllByProfileIdAndStatusOrderByUpdatedAtDesc(profileId, 0);

        // 조회된 Resume 엔티티 리스트를 DTO 리스트로 변환하여 반환
        return resumeList.stream()
                .map(ResumePersonalDTO::fromEntity) // 간단 목록용 fromEntity
                .toList();
    }


    /**
     * 이력서 상세 조회 서비스
     * - Resume + ResumeContent 리스트를 JOIN FETCH로 한 번에 가져옴
     * - 가져온 Resume 엔티티를 DTO로 변환하여 반환
     */
    @Transactional(readOnly = true) // 읽기 전용 트랜잭션 설정 (쓰기 불필요, 성능 최적화)
    public ResumePersonalDTO getResumeDetail(Long resumeId) {

        // ====================== 1. 이력서 조회 ======================
        // 이력서 + 연관된 ResumeContent 항목들까지 함께 조회 (Fetch Join 방식)
        Resume resume = resumeRepository.findWithContentsById(resumeId)
                .orElseThrow(() -> new NotFoundException("이력서를 찾을 수 없습니다."));

        // ====================== 2. 이력서 항목 리스트 변환 ======================
        // 이력서에 포함된 항목(ResumeContent)들을 각각 ResumeContentPersonalDTO로 변환
        // resume.getResumeContentList()의 각 ResumeContent 객체를
        // ResumeContentPersonalDTO.fromEntity(content)로 변환하는 것과 동일
        // 메서드 레퍼런스(::)는 람다식 content -> ResumeContentPersonalDTO.fromEntity(content) 를 간결하게 표현한 문법
        List<ResumeContentPersonalDTO> contentDTOs = resume.getResumeContentList().stream()
                .map(ResumeContentPersonalDTO::fromEntity)
                .toList();

        // ====================== 3. 이력서 → DTO 변환 및 반환 ======================
        return ResumePersonalDTO.fromEntity(resume, contentDTOs); // 여기서 사용됨!
    }


    /**
     * 이력서 생성 서비스
     * - 이력서만 먼저 생성하고 ID 반환 (임시저장용 또는 초안)
     *
     * @param dto 작성 요청 DTO
     * @return 생성된 이력서 ID
     */
    @Transactional
    public Long createResume(ResumePersonalDTO dto) {

        // 1. 필수 값 검증
        if (dto.getTitle() == null || dto.getProfileId() == null) {
            throw new BadRequestException("이력서 제목 또는 프로필 ID는 필수입니다.");
        }

        // 2. 연관 프로필 조회
        Profile profile = profileRepository.findById(dto.getProfileId())
                .orElseThrow(() -> new NotFoundException("프로필을 찾을 수 없습니다."));

        // 3. 자기소개서가 선택된 경우 조회
        CoverLetter coverLetter = null;
        if (dto.getCoverLetterId() != null) {
            coverLetter = coverLetterRepository.findById(dto.getCoverLetterId())
                    .orElseThrow(() -> new NotFoundException("자기소개서를 찾을 수 없습니다."));
        }

        // 4. 이력서 엔티티 생성
        Resume resume = dto.toEntity(profile, coverLetter);

        // 5. 저장 (ID 생성됨)
        resumeRepository.save(resume);

        // 6. 항목이 있는 경우 content 리스트도 저장
        if (dto.getContents() != null && !dto.getContents().isEmpty()) {

            List<ResumeContent> contentList = dto.getContents().stream()
                    .map(content -> content.toEntity(resume))
                    .collect(Collectors.toList());

            resumeContentRepository.saveAll(contentList);
        }

        // 7. 생성된 ID만 반환
        return resume.getId();
    }


    /**
     * 이력서 전체 수정 서비스
     * - 이력서 기본 정보 + 항목 리스트 전체 갱신
     * - 기존 항목들은 모두 삭제 후 새로 저장
     */
    @Transactional
    public void updateResumeAll(Long resumeId, ResumePersonalDTO dto) {

        // 1. 수정 대상 이력서 조회
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new NotFoundException("이력서를 찾을 수 없습니다."));

        // 2. 이력서 기본 정보 수정 (setter 대신 update 메서드 분리 가능)
        resume.setTitle(dto.getTitle());
        resume.setOverview(dto.getOverview());
        resume.setResumeType(dto.getResumeType());
        resume.setExtraLink1(dto.getExtraLink1());
        resume.setExtraLink2(dto.getExtraLink2());
        resume.setStatus(dto.getStatus());

        // 3. 기존 항목 전체 삭제
        resumeContentRepository.deleteAllByResumeId(resumeId);

        // 4. 새 항목 리스트 저장
        if (dto.getContents() != null && !dto.getContents().isEmpty()) { // 항목 리스트가 null이 아니고 비어 있지 않은 경우에만 처리
            List<ResumeContent> contentList = dto.getContents().stream()
                    .map(content -> content.toEntity(resume))
                    .collect(Collectors.toList());

            resumeContentRepository.saveAll(contentList); // 항목 리스트를 DB에 일괄 저장
        }
    }

    // -------------------------------
    @Transactional
    public void setAsPrimaryResume(Long resumeId) {
        // 1. 대상 이력서 조회
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new NotFoundException("이력서를 찾을 수 없습니다."));

        // 2. 삭제된 이력서는 대표 설정 불가
        if (resume.getStatus() == 3) {
            throw new BadRequestException("삭제된 이력서는 대표로 설정할 수 없습니다.");
        }

        Long profileId = resume.getProfile().getId();

        // 3. 동일 프로필의 기존 대표 이력서 모두 초기화
        resumeRepository.clearPrimaryResume(profileId);

        // 4. 현재 이력서를 대표로 설정
        resume.setIsPrimary(true);
    }


    public String generateResumeFileDownloadUrl(Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new NotFoundException("이력서를 찾을 수 없습니다."));

        // 삭제되었거나 파일 타입이 아닌 경우 예외
        if (resume.getStatus() == 3 || resume.getResumeType() != Resume.ResumeType.FILE_UPLOAD) {
            throw new BadRequestException("파일 이력서가 아니거나 삭제된 이력서입니다.");
        }

        String fileKey = resume.getResumeFileKey();
        if (fileKey == null || fileKey.isEmpty()) {
            throw new NotFoundException("파일 이력서가 존재하지 않습니다.");
        }

        // S3 서비스로부터 presigned URL 생성
        return s3Service.generatePresignedUrl(fileKey);
    }

    @Transactional
    public void softDeleteResume(Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new NotFoundException("이력서를 찾을 수 없습니다."));

        // 이미 삭제된 이력서면 예외 처리
        if (resume.getStatus() == Resume.Status.DELETED) {
            throw new BadRequestException("이미 삭제된 이력서입니다.");
        }

        // 상태 변경만 수행 (Soft Delete)
        resume.setStatus(Resume.Status.DELETED);
    }


    @Transactional
    public Long duplicateResume(Long originalId) {
        Resume original = resumeRepository.findById(originalId)
                .orElseThrow(() -> new NotFoundException("이력서를 찾을 수 없습니다."));

        Resume copy = Resume.builder()
                .profile(original.getProfile())
                .title(original.getTitle() + " (복사본)")
                .overview(original.getOverview())
                .resumeType(original.getResumeType())
                .resumeFileKey(original.getResumeFileKey())
                .resumeUrl(original.getResumeUrl())
                .extraLink1(original.getExtraLink1())
                .extraLink2(original.getExtraLink2())
                .coverLetter(original.getCoverLetter())
                .status(0)  // 임시저장 상태
                .isPrimary(false)
                .build();

        resumeRepository.save(copy);

        // 항목 복사
        List<ResumeContent> copiedContents = original.getResumeContentList().stream()
                .map(c -> ResumeContent.builder()
                        .resume(copy)
                        .sectionType(c.getSectionType())
                        .sectionTitle(c.getSectionTitle())
                        .contentOrder(c.getContentOrder())
                        .organization(c.getOrganization())
                        .title(c.getTitle())
                        .field(c.getField())
                        .description(c.getDescription())
                        .dateFrom(c.getDateFrom())
                        .dateTo(c.getDateTo())
                        .contentFileKey(c.getContentFileKey())
                        .contentFileName(c.getContentFileName())
                        .contentFileType(c.getContentFileType())
                        .build())
                .collect(Collectors.toList());


        resumeContentRepository.saveAll(copiedContents);
        return copy.getId();
    }

    @Transactional
    public void updateResumeStatus(Long resumeId, Integer status) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new NotFoundException("이력서를 찾을 수 없습니다."));

        if (status < 0 || status > 2) {
            throw new BadRequestException("올바르지 않은 상태값입니다.");
        }

        resume.setStatus(status);
    }

    public int getViewCount(Long resumeId) {
        if (!resumeRepository.existsById(resumeId)) {
            throw new NotFoundException("이력서를 찾을 수 없습니다.");
        }
        return resumeViewLogRepository.countByResumeId(resumeId);
    }


}
