package com.highfive.meetu.domain.company.personal.dto;

import lombok.*;

/**
 * 관심 기업 등록/해제 요청 DTO
 * - 클라이언트로부터 companyId를 전달받기 위한 용도
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyFollowRequest {
    private Long companyId; // 등록/해제할 기업 ID
}
