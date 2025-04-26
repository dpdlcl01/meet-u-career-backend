package com.highfive.meetu.domain.auth.personal.dto;

import com.highfive.meetu.domain.user.common.entity.Account;
import com.highfive.meetu.domain.user.common.entity.Profile;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoDTO {
    private Long accountId;
    private Long profileId;
    private String name;
    private String email;
    private String profileImageKey;

    public static UserInfoDTO from(Account account, Profile profile) {
        return UserInfoDTO.builder()
            .accountId(account.getId())
            .profileId(profile != null ? profile.getId() : null)
            .name(account.getName())
            .email(account.getEmail())
            .profileImageKey(profile != null ? profile.getProfileImageKey() : null)
            .build();
    }
}