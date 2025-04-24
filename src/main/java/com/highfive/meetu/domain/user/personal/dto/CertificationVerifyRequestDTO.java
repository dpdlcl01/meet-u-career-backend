package com.highfive.meetu.domain.user.personal.dto;

import lombok.*;

@Getter
@Setter
public class CertificationVerifyRequestDTO {
    private String email;
    private String code;
}
