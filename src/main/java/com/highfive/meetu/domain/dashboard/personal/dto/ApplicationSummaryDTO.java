package com.highfive.meetu.domain.dashboard.personal.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationSummaryDTO {
  private long passedDocument;
  private long interview1st;
  private long finalAccepted;
  private long rejected;

}
