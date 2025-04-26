package com.highfive.meetu.domain.portal.common.entity;

import com.highfive.meetu.domain.company.common.entity.Company;
import com.highfive.meetu.domain.job.common.entity.JobPosting;
import com.highfive.meetu.domain.payment.common.entity.Payment;
import com.highfive.meetu.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "advertisement")
public class Advertisement extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jobPostingId", nullable = false)
    private JobPosting jobPosting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "companyId", nullable = false)
    private Company company;

    @Column(nullable = false)
    private Integer adType; // 광고 유형 (BASIC, STANDARD, PREMIUM)

    @Column(nullable = false)
    private Integer status = 1; // 광고 상태 (1: 활성, 2: 일시중지, 3: 만료, 4: 승인대기)

    @Column(nullable = false)
    private Integer durationDays; // 광고 기간 (일)

    @Column(nullable = false)
    private LocalDateTime startDate; // 광고 시작 날짜

    @Column(nullable = false)
    private LocalDateTime endDate; // 광고 종료 날짜

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paymentId")
    private Payment payment; // 결제 정보 (nullable)

    // 광고 유형 코드 (AdType)
    public static class AdType {
        public static final int BASIC = 1;     // 기본 광고
        public static final int STANDARD = 2;  // 표준 광고
        public static final int PREMIUM = 3;   // 프리미엄 광고

        private AdType() {}
    }

    // 상태 코드 (Status)
    public static class Status {
        public static final int ACTIVE = 1;       // 활성
        public static final int PAUSED = 2;       // 일시중지
        public static final int EXPIRED = 3;      // 만료
        public static final int PENDING_APPROVAL = 4; // 승인 대기

        private Status() {}
    }
}
