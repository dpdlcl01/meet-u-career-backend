package com.highfive.meetu.domain.auth.personal.dto;

import lombok.*;

@Getter
@Setter
public class GoogleTokenResponse {
    private String access_token;
    private String expires_in;
    private String scope;
    private String token_type;
    private String id_token;
}