package com.highfive.meetu.domain.payment.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.highfive.meetu.domain.user.common.entity.Account;
import com.highfive.meetu.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 결제 엔티티
 *
 * 연관관계:
 * - Account(1) : Payment(N) - Payment가 주인, @JoinColumn 사용
 * - Payment(1) : Advertisement(1) - 1:1 단방향 관계, Advertisement가 주인, mappedBy 사용
 */
@Entity(name = "payment")
@Table(
        indexes = {
                @Index(name = "idx_payment_accountId", columnList = "accountId"),
                @Index(name = "idx_payment_status", columnList = "status"),
                @Index(name = "idx_payment_createdAt", columnList = "createdAt")
        }
)
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"account", "advertisement"})
public class Payment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accountId", nullable = false)
    private Account account;  // 결제한 계정 (기업 회원)

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;  // 결제 금액

    @Column(nullable = false)
    private Integer status;  // 결제 상태 (FAILED, SUCCESS)

    @Column
    private Integer provider;  // 결제 제공업체 (TOSS, KAKAO)

    @Column
    private Integer method;  // 결제 방식 (CARD, BANK_TRANSFER)

    @Column(length = 100, nullable = false, unique = true)
    private String transactionId;  // 거래 ID (중복 방지)

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;  // 결제 상태 변경일

    @OneToOne(mappedBy = "payment", fetch = FetchType.LAZY)
    private Advertisement advertisement;

    /**
     * 결제 상태
     */
    public static class Status {
        public static final int FAILED = 0;    // 결제 실패
        public static final int SUCCESS = 1;   // 결제 성공
    }

    /**
     * 결제 제공사
     */
    public static class Provider {
        public static final int TOSS = 1;
        public static final int KAKAO = 2;
        public static final int NAVER = 3;
    }

    /**
     * 결제 수단
     */
    public static class Method {
        public static final int CARD = 1;        // 신용/체크카드
        public static final int KAKAO_PAY = 2;   // 카카오페이
        public static final int NAVER_PAY = 3;   // 네이버페이
        public static final int TOSS_PAY = 4;    // 토스페이
    }

}