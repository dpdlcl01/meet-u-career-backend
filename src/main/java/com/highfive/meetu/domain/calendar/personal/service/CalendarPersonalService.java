package com.highfive.meetu.domain.calendar.personal.service;

import com.highfive.meetu.domain.application.common.repository.ApplicationRepository;
import com.highfive.meetu.domain.calendar.common.entity.CalendarEvent;
import com.highfive.meetu.domain.calendar.common.repository.CalendarEventRepository;
import com.highfive.meetu.domain.calendar.personal.dto.CalendarPersonalDTO;
import com.highfive.meetu.domain.calendar.personal.dto.PublicCalendarItemDTO;
import com.highfive.meetu.domain.company.common.entity.Company;
import com.highfive.meetu.domain.company.common.repository.CompanyFollowRepository;
import com.highfive.meetu.domain.company.common.repository.CompanyRepository;
import com.highfive.meetu.domain.job.common.entity.JobPosting;
import com.highfive.meetu.domain.job.common.repository.BookmarkRepository;
import com.highfive.meetu.domain.job.common.repository.JobPostingRepository;
import com.highfive.meetu.domain.user.common.entity.Account;
import com.highfive.meetu.domain.user.common.repository.AccountRepository;
import com.highfive.meetu.global.common.exception.BadRequestException;
import com.highfive.meetu.global.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CalendarPersonalService {

    private final CalendarEventRepository calendarEventRepository;
    private final AccountRepository accountRepository;
    private final CompanyRepository companyRepository;
    private final ApplicationRepository applicationRepository;
    private final CompanyFollowRepository companyFollowRepository;
    private final BookmarkRepository bookmarkRepository;
    private final JobPostingRepository jobPostingRepository;

    // 비로그인시
    @Transactional(readOnly = true)
    public List<PublicCalendarItemDTO> getPublicScheduleList() {
        LocalDateTime now = LocalDateTime.now();
        List<JobPosting> soonClosing = jobPostingRepository
                .findTop10ByExpirationDateAfterAndStatusOrderByExpirationDateAsc(now, 2); // status 2: 활성 공고

        return soonClosing.stream()
                .map(job -> PublicCalendarItemDTO.builder()
                        .title(job.getTitle())
                        .expirationDate(job.getExpirationDate())
                        .companyName(job.getCompany().getName())
                        .build()
                ).toList();
    }
    


    // 모든 유형의 회원 일정 가져오기
    @Transactional(readOnly = true)
    public List<CalendarPersonalDTO> getFullScheduleForAccount(Long accountId) {

        // [1] 개인 등록 일정 (eventType = 4)
        List<CalendarPersonalDTO> personalSchedules = calendarEventRepository
                .findAllByAccount_IdAndEventType(accountId, CalendarEvent.EventType.PERSONAL_EVENT)
                .stream()
                .map(CalendarPersonalDTO::fromEntity)
                .toList();

        // [2] 지원 마감 일정 (eventType = 1)
        List<CalendarPersonalDTO> applicationSchedules = applicationRepository
                .findByProfile_Account_Id(accountId)
                .stream()
                .map(app -> {
                    var job = app.getJobPosting();
                    var company = job.getCompany();
                    return CalendarPersonalDTO.builder()
                            .eventType(CalendarEvent.EventType.APPLICATION_DEADLINE)
                            .title(job.getTitle() + " 지원 마감")
                            .description(null)
                            .relatedId(job.getId())
                            .companyId(company.getId())
                            .companyName(company.getName())
                            .startDateTime(job.getExpirationDate())
                            .endDateTime(job.getExpirationDate())
                            .isAllDay(true)
                            .updatedAt(job.getUpdatedAt())
                            .build();
                })
                .toList();

        // [3] 스크랩 마감 일정
        List<CalendarPersonalDTO> bookmarkSchedules = bookmarkRepository
                .findByProfile_Account_Id(accountId)
                .stream()
                .map(bookmark -> {
                    var job = bookmark.getJobPosting();
                    var company = job.getCompany();
                    return CalendarPersonalDTO.builder()
                            .eventType(CalendarEvent.EventType.BOOKMARK_DEADLINE)
                            .title(job.getTitle() + " 스크랩 마감")
                            .description(null)
                            .relatedId(job.getId())
                            .companyId(company.getId())
                            .companyName(company.getName())
                            .startDateTime(job.getExpirationDate())
                            .endDateTime(job.getExpirationDate())
                            .isAllDay(true)
                            .updatedAt(job.getUpdatedAt())
                            .build();
                })
                .toList();

        // [4] 팔로우한 기업의 공고 일정 (접수 시작/마감)
        List<Long> companyIds = companyFollowRepository.findByProfile_Account_Id(accountId)
                .stream()
                .map(f -> f.getCompany().getId())
                .toList();


        List<CalendarPersonalDTO> companyJobSchedules = Collections.emptyList();

        if (!companyIds.isEmpty()) {
            companyJobSchedules = jobPostingRepository
                    .findByCompany_IdInAndStatus(companyIds, 2) // 2 = 활성 공고
                    .stream()
                    .flatMap(job -> Stream.of(
                            CalendarPersonalDTO.builder()
                                    .eventType(CalendarEvent.EventType.COMPANY_EVENT)
                                    .title(job.getTitle() + " 접수 시작")
                                    .description(null)
                                    .relatedId(job.getId())
                                    .companyId(job.getCompany().getId())
                                    .companyName(job.getCompany().getName())
                                    .startDateTime(job.getOpeningDate())
                                    .endDateTime(job.getOpeningDate())
                                    .isAllDay(true)
                                    .updatedAt(job.getUpdatedAt())
                                    .build(),
                            CalendarPersonalDTO.builder()
                                    .eventType(CalendarEvent.EventType.COMPANY_EVENT)
                                    .title(job.getTitle() + " 접수 마감")
                                    .description(null)
                                    .relatedId(job.getId())
                                    .companyId(job.getCompany().getId())
                                    .companyName(job.getCompany().getName())
                                    .startDateTime(job.getExpirationDate())
                                    .endDateTime(job.getExpirationDate())
                                    .isAllDay(true)
                                    .updatedAt(job.getUpdatedAt())
                                    .build()
                    )).toList();
        }

        // 각 리스트의 size 출력
        System.out.println("personalSchedules: " + personalSchedules.size());
        System.out.println("applicationSchedules: " + applicationSchedules.size());
        System.out.println("bookmarkSchedules: " + bookmarkSchedules.size());
        System.out.println("companyIds: " + companyIds.size());
        System.out.println("companyJobSchedules: " + companyJobSchedules.size());


        // 통합 후 정렬하여 반환
        return Stream.of(
                        personalSchedules,
                        applicationSchedules,
                        bookmarkSchedules,
                        companyJobSchedules
                ).flatMap(List::stream)
                .sorted(Comparator.comparing(CalendarPersonalDTO::getStartDateTime))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CalendarPersonalDTO> getFullScheduleForCompany(Long accountId) {

        // 1. 내 기업 정보 조회
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("사용자 계정이 존재하지 않습니다."));
        Company company = account.getCompany();
        if (company == null) {
            throw new BadRequestException("등록된 회사 정보가 없습니다.");
        }

        Long companyId = company.getId();

        // 2. 내 기업이 등록한 공고 리스트
        List<JobPosting> jobPostings = jobPostingRepository.findByCompany_IdAndStatus(companyId, 2); // 2 = 활성 공고

        List<CalendarPersonalDTO> jobPostingSchedules = jobPostings.stream()
                .flatMap(job -> Stream.of(
                        CalendarPersonalDTO.builder()
                                .eventType(CalendarEvent.EventType.COMPANY_EVENT)
                                .title(job.getTitle() + " 접수 시작")
                                .startDateTime(job.getOpeningDate())
                                .endDateTime(job.getOpeningDate())
                                .isAllDay(true)
                                .relatedId(job.getId())
                                .companyId(companyId)
                                .companyName(company.getName())
                                .build(),

                        CalendarPersonalDTO.builder()
                                .eventType(CalendarEvent.EventType.COMPANY_EVENT)
                                .title(job.getTitle() + " 접수 마감")
                                .startDateTime(job.getExpirationDate())
                                .endDateTime(job.getExpirationDate())
                                .isAllDay(true)
                                .relatedId(job.getId())
                                .companyId(companyId)
                                .companyName(company.getName())
                                .build()
                )).toList();

        // 3. 내가 등록한 기업 커스텀 일정 (eventType = 4)
        List<CalendarPersonalDTO> companyCustomSchedules = calendarEventRepository
                .findAllByAccount_IdAndEventType(accountId, CalendarEvent.EventType.PERSONAL_EVENT)
                .stream()
                .map(CalendarPersonalDTO::fromEntity)
                .toList();

        // 4. 통합 후 정렬
        return Stream.of(companyCustomSchedules, jobPostingSchedules)
                .flatMap(List::stream)
                .sorted(Comparator.comparing(CalendarPersonalDTO::getStartDateTime))
                .toList();
    }



    /**
     * 일정 등록
     */
    @Transactional
    public Long addSchedule(Long accountId, CalendarPersonalDTO dto) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("계정이 존재하지 않습니다."));

        Company company = null;
        if (dto.getCompanyId() != null) {
            company = companyRepository.findById(dto.getCompanyId())
                    .orElseThrow(() -> new NotFoundException("회사 정보가 존재하지 않습니다."));
        }

        CalendarEvent event = dto.toEntity(account, company);
        calendarEventRepository.save(event);
        return event.getId();
    }


    /**
     * 일정 상세 조회
     */
    @Transactional(readOnly = true)
    public CalendarPersonalDTO getScheduleDetail(Long id) {
        CalendarEvent event = calendarEventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("일정을 찾을 수 없습니다."));
        return CalendarPersonalDTO.fromEntity(event);
    }

    /**
     * 일정 수정
     */
    @Transactional
    public void updateSchedule(CalendarPersonalDTO dto) {
        CalendarEvent event = calendarEventRepository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("수정할 일정을 찾을 수 없습니다."));

        // 커스텀 일정만 수정 가능
        if (!event.getEventType().equals(CalendarEvent.EventType.PERSONAL_EVENT)) {
            throw new BadRequestException("개인 커스텀 일정만 수정할 수 있습니다.");
        }

        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setEventType(dto.getEventType());
        event.setStartDateTime(dto.getStartDateTime());
        event.setEndDateTime(dto.getEndDateTime());
        event.setIsAllDay(dto.getIsAllDay());
    }


    /**
     * 일정 삭제
     */
    @Transactional
    public void deleteSchedule(Long id) {
        CalendarEvent event = calendarEventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("삭제할 일정을 찾을 수 없습니다."));

        // 커스텀 일정만 삭제 가능
        if (!event.getEventType().equals(CalendarEvent.EventType.PERSONAL_EVENT)) {
            throw new BadRequestException("개인 커스텀 일정만 삭제할 수 있습니다.");
        }

        calendarEventRepository.delete(event);
    }


}
