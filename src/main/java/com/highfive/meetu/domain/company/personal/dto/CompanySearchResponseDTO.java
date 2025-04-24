package com.highfive.meetu.domain.company.personal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CompanySearchResponseDTO {
    private Long id;
    private String name;
    private String businessNumber;
    private String representativeName;
    private String industry;
    private String foundedDate;
    private Integer numEmployees;
    private Long revenue;
    private String website;
    private String logoUrl;
    private String address;
    private LocalDateTime createAt;
}
