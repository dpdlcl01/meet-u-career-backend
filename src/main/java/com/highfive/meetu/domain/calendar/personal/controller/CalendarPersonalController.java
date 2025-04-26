package com.highfive.meetu.domain.calendar.personal.controller;

import com.highfive.meetu.domain.calendar.personal.dto.CalendarPersonalDTO;
import com.highfive.meetu.domain.calendar.personal.dto.PublicCalendarItemDTO;
import com.highfive.meetu.domain.calendar.personal.service.CalendarPersonalService;
import com.highfive.meetu.global.common.response.ResultData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/personal/calendar")
public class CalendarPersonalController {

    private final CalendarPersonalService calendarPersonalService;

    /**
     * 일정 관리 페이지 진입시 일정 목록
     */
    @GetMapping("/list")
    public ResultData<?> getCalendarList() {
        try {
            // 로그인 상태 → 내 전체 일정 (개인회원, 기업회원 동일 방식)
            //Long accountId = SecurityUtil.getAccountId();
            Long accountId = 2L;
            //Long accountId = null;
            List<CalendarPersonalDTO> schedules = calendarPersonalService.getFullScheduleForAccount(accountId);
            return ResultData.success(schedules.size(), schedules);

        } catch (RuntimeException e) {
            // 비로그인 상태 → 마감 공고 일정 일부 띄우기
            List<PublicCalendarItemDTO> guestSchedules = calendarPersonalService.getPublicScheduleList();
            return ResultData.success(guestSchedules.size(), guestSchedules);
        }
    }

    /**
     * [기업회원용] 기업 커스텀 일정 + 공고 일정 조회
     */
    @GetMapping("/business/list")
    public ResultData<List<CalendarPersonalDTO>> getCompanySchedule() {
        //Long accountId = SecurityUtil.getAccountId(); // 현재 로그인한 기업회원 ID
        Long accountId = 6L;
        List<CalendarPersonalDTO> schedules = calendarPersonalService.getFullScheduleForCompany(accountId);
        return ResultData.success(schedules.size(), schedules);
    }

    /**
     * 일정 등록
     */
    @PostMapping("/create")
    public ResultData<Long> createCalendar(@RequestBody CalendarPersonalDTO dto) {

        //Long accountId = SecurityUtil.getAccountId(); // 인증된 사용자 ID
        //Long accountId = 2L; 개인회원
        Long accountId = 6L; // 기업회원

        Long id = calendarPersonalService.addSchedule(accountId, dto);
        return ResultData.success(1, id);
    }

    /**
     * 일정 상세 조회
     */
    @GetMapping("/detail/{calendarEventId}")
    public ResultData<CalendarPersonalDTO> getCalendarDetail(@PathVariable Long calendarEventId) {
        CalendarPersonalDTO dto = calendarPersonalService.getScheduleDetail(calendarEventId);
        return ResultData.success(1, dto);
    }

    /**
     * 일정 수정
     */
    @PostMapping("/update/{calendarEventId}")
    public ResultData<String> updateCalendar(@PathVariable Long calendarEventId, @RequestBody CalendarPersonalDTO dto) {
        dto.setId(calendarEventId); // calendarId를 dto에 세팅
        calendarPersonalService.updateSchedule(dto);
        return ResultData.success(1, "일정이 수정되었습니다.");
    }

    /**
     * 일정 삭제
     */
    @PostMapping("/delete/{calendarEventId}")
    public ResultData<String> deleteCalendar(@PathVariable Long calendarEventId) {
        calendarPersonalService.deleteSchedule(calendarEventId);
        return ResultData.success(1, "일정이 삭제되었습니다.");
    }
}

