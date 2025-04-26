package com.highfive.meetu.domain.dashboard.personal.dto;

import com.highfive.meetu.domain.user.common.entity.Account;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccountDTO {
  private Long id;
  private String userId;
  private String email;
  private String name;
  private String phone;
  private int accountType;
  private int status;

  public static AccountDTO from(Account account) {
    return AccountDTO.builder()
        .id(account.getId())
        .userId(account.getUserId())
        .email(account.getEmail())
        .name(account.getName())
        .phone(account.getPhone())
        .accountType(account.getAccountType())
        .status(account.getStatus())
        .build();
  }
}
