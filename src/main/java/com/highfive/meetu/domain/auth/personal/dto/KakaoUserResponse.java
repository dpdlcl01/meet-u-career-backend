package com.highfive.meetu.domain.auth.personal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
public class KakaoUserResponse {
    private Long id;
    @JsonProperty("kakao_account") // JSON 매핑 키 명시
    private KakaoAccount kakaoAccount;

    @Getter @Setter
    public static class KakaoAccount {
        private String email;
        private Profile profile;

        @Getter @Setter
        public static class Profile {
            private String nickname;
        }
    }
}
