package com.highfive.meetu.domain.portal.dto;

import com.highfive.meetu.domain.job.personal.dto.JobPostingDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MainPageResponseDTO {
    private boolean isLoggedIn;
    private List<JobPostingDTO> recommendations;
    private List<JobPostingDTO> popular;
    private List<JobPostingDTO> latest;
    private List<JobPostingDTO> mostApplied;
}