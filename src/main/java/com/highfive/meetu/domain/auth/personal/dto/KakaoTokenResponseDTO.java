package com.highfive.meetu.domain.auth.personal.dto;

import lombok.*;

@Getter
@Setter
public class KakaoTokenResponseDTO {
    private String access_token;
    private String token_type;
    private String refresh_token;
    private Long expires_in;
    private String scope;
}
