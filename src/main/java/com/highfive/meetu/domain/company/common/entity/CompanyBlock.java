package com.highfive.meetu.domain.company.common.entity;

import com.highfive.meetu.domain.user.common.entity.Profile;
import com.highfive.meetu.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * 기업 차단 엔티티
 *
 * 연관관계:
 * - Profile(1) : CompanyBlock(N) - CompanyBlock이 주인, @JoinColumn 사용
 * - Company(1) : CompanyBlock(N) - CompanyBlock이 주인, @JoinColumn 사용
 */
@Entity(name = "companyBlock")
@Table(
        indexes = {
                @Index(name = "idx_company_block_profileId", columnList = "profileId"),
                @Index(name = "idx_company_block_companyId", columnList = "companyId")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_profile_company", columnNames = {"profileId", "companyId"})
        }
)
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"profile", "company"})
public class CompanyBlock extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profileId", nullable = false)
    private Profile profile;  // 차단을 설정한 구직자의 프로필 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "companyId", nullable = false)
    private Company company;  // 차단된 기업
}
