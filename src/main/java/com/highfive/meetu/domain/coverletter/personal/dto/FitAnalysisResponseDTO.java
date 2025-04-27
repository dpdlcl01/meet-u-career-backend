package com.highfive.meetu.domain.coverletter.personal.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FitAnalysisResponseDTO {
    private Double fitScore;
    private String comment;
}
