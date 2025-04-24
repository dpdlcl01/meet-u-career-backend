package com.highfive.meetu.domain.auth.personal.dto;

import lombok.*;

@Getter
@NoArgsConstructor
public class NaverUserResponse {
    private Long id;
    private NaverAccount response;

    @Getter
    @NoArgsConstructor
    public static class NaverAccount {
        private String name;
        private String email;
        private Profile profile;

        @Getter @Setter
        public static class Profile {
            private String nickname;
            private String profile_image;
        }
    }
}
