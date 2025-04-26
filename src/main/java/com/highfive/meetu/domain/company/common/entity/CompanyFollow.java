package com.highfive.meetu.domain.company.common.entity;

import com.highfive.meetu.domain.user.common.entity.Profile;
import com.highfive.meetu.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * 관심 기업 엔티티
 *
 * 연관관계:
 * - Profile(1) : CompanyFollow(N) - CompanyFollow가 주인, @JoinColumn 사용
 * - Company(1) : CompanyFollow(N) - CompanyFollow가 주인, @JoinColumn 사용
 */
@Entity(name = "companyFollow")
@Table(
        indexes = {
                @Index(name = "idx_company_follow_profileId", columnList = "profileId"),
                @Index(name = "idx_company_follow_companyId", columnList = "companyId")
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
public class CompanyFollow extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profileId", nullable = false)
    private Profile profile;  // 팔로우한 사용자 (Profile 기준)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "companyId", nullable = false)
    private Company company;  // 팔로우한 기업
}