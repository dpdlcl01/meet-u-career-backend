package com.highfive.meetu.domain.user.personal.dto;

import com.highfive.meetu.domain.user.common.entity.Account;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalSignUpRequestDTO {
    private String userId;
    private String email;
    private String password;
    private String phone;
    private String name;
    private LocalDate birthday;
    private Integer status;

    public Account toEntity(String encodedPassword) {
        return Account.builder()
            .userId(userId)
            .email(email)
            .password(encodedPassword)
            .phone(phone)
            .name(name)
            .birthday(birthday)
            .accountType(0)
            .status(status != null ? status : Status.ACTIVE)
            .build();
    }

    public static PersonalSignUpRequestDTO fromEntity(Account account) {
        return PersonalSignUpRequestDTO.builder()
            .userId(account.getUserId())
            .email(account.getEmail())
            .phone(account.getPhone())
            .name(account.getName())
            .birthday(account.getBirthday())
            .status(account.getStatus())
            .build();
    }

    /**
     * 상태값
     */
    public static class Status {
        public static final int ACTIVE = 0;  // 활성 상태
        public static final int INACTIVE = 1;   // 비활성
        public static final int WAITING = 2; // 기업계정 승인 대기 중
        public static final int REJECTED = 3; // 기업계정 반려됨
    }
}
