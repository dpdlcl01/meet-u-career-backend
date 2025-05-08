package com.highfive.meetu.domain.company.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    @Column(length = 100)
    private String representativeName;  // 대표자명

    @Column(length = 255, nullable = false)
    private String industry;  // 업종

    @Column
    private LocalDate foundedDate;  // 설립일

    @Column
    private Integer numEmployees;  // 직원 수

    @Column
    private Long revenue;  // 매출액

    @Column(length = 500)
    private String website;  // 회사 웹사이트 URL

    @Column(length = 500)
    private String logoKey;  // 기업 로고 이미지 URL

    @Column(length = 500)
    private String address;  // 회사 주소

    @Column(length = 50)
    private String companyType;  // 기업 형태 (예: 대기업 등)

    @Column(length = 50)
    private String corpCode;  // API 연동용 기업 코드

    @Column
    private Long operatingProfit;  // 최근 연도 영업이익

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;  // 기업 정보 수정일

    @Column(nullable = false)
    private Integer status;  // 기업 상태 (ACTIVE, INACTIVE)

    @Column
    private Long avgAnnualSalary;   // 평균연봉

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

    // 상태 코드 정의
    public static class Status {
        public static final int ACTIVE = 0;   // 정상
        public static final int INACTIVE = 1; // 비활성화됨
    }
}