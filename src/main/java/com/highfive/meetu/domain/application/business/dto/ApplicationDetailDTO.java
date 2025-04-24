package com.highfive.meetu.domain.application.business.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApplicationDetailDTO {
  private Long applicationId;
  private String applicationName;
  private String applicationEmail;
  private String applicationLocation;
}