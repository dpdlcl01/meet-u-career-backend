package com.highfive.meetu.domain.company.personal.dto;

import com.highfive.meetu.domain.company.common.entity.CompanyBlock;
import lombok.*;

import java.time.LocalDateTime;

/**
 * [개인회원] 차단한 기업 조회용 DTO
 * - 차단 목록 화면에서 사용
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyBlockResponseDTO {
    private Long companyId;    // 차단된 기업 ID
    private String companyName; // 차단된 기업명
    private String representativeName;
    private String industry;
    private String logoUrl;
    private String address;
    private LocalDateTime createAt;

    /**
     * CompanyBlock 엔티티 → CompanyBlockResponseDTO 변환
     *
     * @param block CompanyBlock 엔티티
     * @return 변환된 CompanyBlockResponseDTO
     */
    public static CompanyBlockResponseDTO fromEntity(CompanyBlock block) {
        return CompanyBlockResponseDTO.builder()
            .companyId(block.getCompany().getId())
            .companyName(block.getCompany().getName())
            .representativeName(block.getCompany().getRepresentativeName())
            .industry(block.getCompany().getIndustry())
            .logoUrl(block.getCompany().getLogoKey())
            .address(block.getCompany().getAddress())
            .createAt(block.getCreatedAt())
            .build();
    }
}
