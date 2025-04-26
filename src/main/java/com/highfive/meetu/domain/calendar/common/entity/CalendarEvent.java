package com.highfive.meetu.domain.calendar.common.entity;

import com.highfive.meetu.domain.company.common.entity.Company;
import com.highfive.meetu.domain.user.common.entity.Account;
import com.highfive.meetu.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

/**
 * 캘린더 이벤트 엔티티
 *
 * 연관관계:
 * - Account(1) : CalendarEvent(N) - CalendarEvent가 주인, @JoinColumn 사용
 * - Company(1) : CalendarEvent(N) - CalendarEvent가 주인, @JoinColumn 사용
 */
@Entity(name = "calendarEvent")
@Table(
        indexes = {
                @Index(name = "idx_calendar_accountId", columnList = "accountId"),
                @Index(name = "idx_calendar_startDate", columnList = "startDateTime")
        }
)
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"account", "company"})
public class CalendarEvent extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accountId", nullable = false)
    private Account account;  // 일정 등록한 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "companyId")
    private Company company;  // 일정이 속한 기업 (NULL 가능)

    @Column(nullable = false)
    private Integer eventType;  // 일정 유형 (1:지원 마감, 2:북마크 마감, 3:관심기업 공고 일정, 4:개인일정)

    @Column(length = 255, nullable = false)
    private String title;  // 일정 제목

    @Column(columnDefinition = "TEXT")
    private String description;  // 일정 설명

    @Column
    private Long relatedId;  // 관련된 데이터 ID (지원 ID, 공고 ID 등)

    @Column(nullable = false)
    private LocalDateTime startDateTime;  // 일정 시작 날짜 및 시간

    @Column(nullable = false)
    private LocalDateTime endDateTime;  // 일정 종료 날짜 및 시간

    @Column(nullable = false)
    private Boolean isAllDay;  // 종일 일정 여부

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;  // 일정 수정일

    // 이벤트 유형 상수
    public static class EventType {
        public static final int APPLICATION_DEADLINE = 1;   // 지원 마감
        public static final int BOOKMARK_DEADLINE = 2;      // 북마크 마감
        public static final int COMPANY_EVENT = 3;          // 관심기업 공고 일정
        public static final int PERSONAL_EVENT = 4;         // 개인 커스텀 일정
    }

}