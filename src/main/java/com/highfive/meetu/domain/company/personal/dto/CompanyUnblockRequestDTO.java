package com.highfive.meetu.domain.company.personal.dto;

import lombok.*;

/**
 * 기업 차단 해제 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyUnblockRequestDTO {
    private Long companyId;  // 차단 해제할 기업 ID
}
