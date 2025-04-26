package com.highfive.meetu.domain.dashboard.personal.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecentApplicationDTO {
    private String companyName;
    private String jobTitle;
    private Integer status; // 0: 지원완료, 1: 서류통과, 2: 1차면접, 3: 최종합격, 4: 불합격 등
}