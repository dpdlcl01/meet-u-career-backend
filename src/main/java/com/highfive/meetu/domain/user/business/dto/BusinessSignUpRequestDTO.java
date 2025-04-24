package com.highfive.meetu.domain.user.business.dto;

import com.highfive.meetu.domain.company.common.entity.Company;
import com.highfive.meetu.domain.user.common.entity.Account;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessSignUpRequestDTO {
    // Account
    private String email;
    private String password;
    private String phone;
    private String name;
    private LocalDate birthday;

    // Company
    private String businessNumber;
    private String companyName;
    private String address;
    private String industry;
    private LocalDate foundedDate;
    private Integer numEmployees;
    private Long revenue;
    private String website;
    private String logoKey;
    
    // 상태값
    private Integer status;

    // Company 생성
    public Company toCompany() {
        return Company.builder()
            .businessNumber(businessNumber)
            .name(companyName)
            .address(address)
            .industry(industry)
            .foundedDate(foundedDate)
            .numEmployees(numEmployees)
            .revenue(revenue)
            .website(website)
            .logoKey(logoKey)
            .status(status != null ? status : Status.WAITING)
            .build();
    }

    // Account 생성
    public Account toAccount(String encodedPassword, Company company) {
        return Account.builder()
            .email(email)
            .password(encodedPassword)
            .name(name)
            .phone(phone)
            .birthday(birthday)
            .accountType(1) // 기업 회원
            .status(status != null ? status : Status.WAITING)
            .company(company)
            .build();
    }

    // Entity → DTO 변환
    public static BusinessSignUpRequestDTO from(Account account, Company company) {
        return BusinessSignUpRequestDTO.builder()
            .email(account.getEmail())
            .phone(account.getPhone())
            .name(account.getName())
            .birthday(account.getBirthday())
            .businessNumber(company.getBusinessNumber())
            .companyName(company.getName())
            .address(company.getAddress())
            .industry(company.getIndustry())
            .foundedDate(company.getFoundedDate())
            .numEmployees(company.getNumEmployees())
            .revenue(company.getRevenue())
            .website(company.getWebsite())
            .logoKey(company.getLogoKey())
            .status(account.getStatus())
            .build();
    }

    /**
     * 상태값
     */
    public static class Status {
        public static final int ACTIVE = 0;  // 활성 상태
        public static final int INACTIVE = 1;   // 비활성
        public static final int WAITING = 2; // 기업계정 승인 대기 중
        public static final int REJECTED = 3; // 기업계정 반려됨
    }
}
