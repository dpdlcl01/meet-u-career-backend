package com.highfive.meetu.domain.calendar.common.repository;

import com.highfive.meetu.domain.calendar.common.entity.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {

    // 계정의 모든 일정 조회
    List<CalendarEvent> findAllByAccount_Id(Long accountId);

    // 개인 회원의 특정 날짜 일정 존재 여부
    boolean existsByAccount_IdAndStartDateTimeBetween(Long accountId, LocalDateTime start, LocalDateTime end);

    List<CalendarEvent> findAllByAccount_IdAndEventType(Long accountId, Integer eventType);

    List<CalendarEvent> findAllByCompany_IdInAndEventType(List<Long> companyIds, Integer eventType);


}
