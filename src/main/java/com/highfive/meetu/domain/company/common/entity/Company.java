package com.highfive.meetu.domain.company.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.highfive.meetu.domain.company.common.type.CompanyTypes;
import com.highfive.meetu.domain.job.common.entity.JobPosting;
import com.highfive.meetu.domain.user.common.entity.Account;
import com.highfive.meetu.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 기업 엔티티
 *
 * 연관관계:
 * - Company(1) : Account(N) - Company가 비주인, mappedBy 사용
 * - Company(1) : JobPosting(N) - Company가 비주인, mappedBy 사용
 */
@Entity(name = "company")
@Table(
        indexes = {
                @Index(name = "idx_company_status", columnList = "status")
        }
)
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"accountList", "jobPostingList"})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Company extends BaseEntity {

    @Column(length = 255, nullable = false)
    private String name;  // 회사명

    @Column(length = 20, nullable = false, unique = true)
    private String businessNumber;  // 사업자등록번호

    @Column(length = 255, nullable = false)
    private String industry;  // 업종

    @Column(nullable = false)
    private LocalDate foundedDate;  // 설립일

    @Column(nullable = false)
    private Integer numEmployees;  // 직원 수

    @Column(nullable = false)
    private Long revenue;  // 매출액

    @Column(length = 500, nullable = false)
    private String website;  // 회사 웹사이트 URL

    @Column(length = 500)
    private String logoUrl;  // 기업 로고 이미지 URL

    @Column(length = 500, nullable = false)
    private String address;  // 회사 주소

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;  // 기업 정보 수정일

    @Column(nullable = false)
    private CompanyTypes.Status status;  // 기업 상태 (ACTIVE, INACTIVE) - 컨버터 자동 적용

    @BatchSize(size = 20)
    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"company"})
    @Builder.Default
    private List<Account> accountList = new ArrayList<>();

    @BatchSize(size = 20)
    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"company"})
    @Builder.Default
    private List<JobPosting> jobPostingList = new ArrayList<>();

    /**
     * 기업 상태 업데이트
     */
    public void updateStatus(CompanyTypes.Status newStatus) {
        this.status = newStatus;
    }

    /**
     * 기업이 활성 상태인지 확인
     */
    public boolean isActive() {
        return this.status == CompanyTypes.Status.ACTIVE;
    }
}