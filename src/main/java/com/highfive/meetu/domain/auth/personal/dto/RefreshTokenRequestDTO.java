package com.highfive.meetu.domain.auth.personal.dto;

import lombok.*;

@Getter
@NoArgsConstructor
public class RefreshTokenRequestDTO {
    private String refreshToken;
}
