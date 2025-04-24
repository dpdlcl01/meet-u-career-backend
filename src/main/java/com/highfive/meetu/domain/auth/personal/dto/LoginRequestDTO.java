package com.highfive.meetu.domain.auth.personal.dto;

import lombok.*;

@Getter
@NoArgsConstructor
public class LoginRequestDTO {
    private String userId;
    private String password;
}
