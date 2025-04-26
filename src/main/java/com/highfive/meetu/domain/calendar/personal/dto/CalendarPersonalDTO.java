package com.highfive.meetu.domain.calendar.personal.dto;

import com.highfive.meetu.domain.calendar.common.entity.CalendarEvent;
import com.highfive.meetu.domain.company.common.entity.Company;
import com.highfive.meetu.domain.user.common.entity.Account;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarPersonalDTO {

    private Long id;

    private Integer eventType;  // 1~4: 지원 마감, 스크랩 마감, 기업 이벤트, 개인 일정

    private String title;

    private String description;

    private Long relatedId;  // 지원서 ID 또는 공고 ID 등

    private Long companyId;
    private String companyName;  // 캘린더 표시용 (nullable)

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    private Boolean isAllDay;

    private LocalDateTime updatedAt;


    public CalendarEvent toEntity(Account account, Company company) {
        return CalendarEvent.builder()
                .account(account)
                .company(company)
                .eventType(this.eventType)
                .title(this.title)
                .description(this.description)
                .relatedId(this.relatedId)
                .startDateTime(this.startDateTime)
                .endDateTime(this.endDateTime)
                .isAllDay(this.isAllDay)
                .build();
    }

    public static CalendarPersonalDTO fromEntity(CalendarEvent event) {
        return CalendarPersonalDTO.builder()
                .id(event.getId())
                .eventType(event.getEventType())
                .title(event.getTitle())
                .description(event.getDescription())
                .relatedId(event.getRelatedId())
                .companyId(event.getCompany() != null ? event.getCompany().getId() : null)
                .companyName(event.getCompany() != null ? event.getCompany().getName() : null)
                .startDateTime(event.getStartDateTime())
                .endDateTime(event.getEndDateTime())
                .isAllDay(event.getIsAllDay())
                .updatedAt(event.getUpdatedAt())
                .build();
    }

}
