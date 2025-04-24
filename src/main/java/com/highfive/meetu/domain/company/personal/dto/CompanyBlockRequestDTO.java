package com.highfive.meetu.domain.company.personal.dto;

import java.util.List;

public class CompanyBlockRequestDTO {
    private List<Long> companyIds;

    // Getter and Setter
    public List<Long> getCompanyIds() {
        return companyIds;
    }

    public void setCompanyIds(List<Long> companyIds) {
        this.companyIds = companyIds;
    }
}