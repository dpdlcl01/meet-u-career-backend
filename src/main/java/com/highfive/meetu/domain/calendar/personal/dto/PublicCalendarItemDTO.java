package com.highfive.meetu.domain.calendar.personal.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicCalendarItemDTO {
    private String title;
    private LocalDateTime expirationDate;
    private String companyName;
}
