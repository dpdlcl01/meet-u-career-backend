package com.highfive.meetu.domain.payment.common.entity;

import com.highfive.meetu.domain.company.common.entity.Company;
import com.highfive.meetu.domain.job.common.entity.JobPosting;
import com.highfive.meetu.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * 광고 엔티티
 *
 * 연관관계:
 * - JobPosting(1) : Advertisement(N) - Advertisement가 주인, @JoinColumn 사용
 * - Company(1) : Advertisement(N) - Advertisement가 주인, @JoinColumn 사용
 * - Advertisement(1) : Payment(1) - 단방향 1:1 관계, Advertisement가 주인
 */
@Entity(name = "advertisement")
@Table(
        indexes = {
                @Index(name = "idx_ad_jobPostingId", columnList = "jobPostingId"),
                @Index(name = "idx_ad_companyId", columnList = "companyId"),
                @Index(name = "idx_ad_startDate", columnList = "startDate"),
                @Index(name = "idx_ad_endDate", columnList = "endDate"),
                @Index(name = "idx_ad_status", columnList = "status")
        }
)
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"jobPosting", "company", "payment"})
public class Advertisement extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jobPostingId", nullable = false)
    private JobPosting jobPosting;  // 광고 대상 공고

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "companyId", nullable = false)
    private Company company;  // 광고를 신청한 기업

    @Column(nullable = false)
    private Integer adType;  // 광고 유형 (1: BASIC, 2: STANDARD, 3: PREMIUM)

    @Column(nullable = false)
    private Integer status;  // 광고 상태 (1: 활성, 2: 일시중지, 3: 만료, 4: 승인대기)

    @Column(nullable = false)
    private Integer durationDays;  // 광고 기간 (단위: 일)

    @Column(nullable = false)
    private LocalDateTime startDate;  // 광고 시작 날짜

    @Column(nullable = false)
    private LocalDateTime endDate;  // 광고 종료 날짜

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paymentId", unique = true)
    private Payment payment;  // 광고 결제 (NULL 가능)

    /**
     * 광고 유형
     */
    public static class AdType {
        public static final int BASIC = 1;      // 기본 광고
        public static final int STANDARD = 2;   // 스탠다드 광고
        public static final int PREMIUM = 3;    // 프리미엄 광고
    }

    /**
     * 광고 상태
     */
    public static class Status {
        public static final int ACTIVE = 1;            // 광고 진행 중
        public static final int PAUSED = 2;            // 광고 일시 중지
        public static final int EXPIRED = 3;           // 광고 만료
        public static final int PENDING_APPROVAL = 4;  // 관리자 승인 대기
    }
}