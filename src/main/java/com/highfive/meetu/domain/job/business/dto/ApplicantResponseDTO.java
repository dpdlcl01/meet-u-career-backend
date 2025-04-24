package com.highfive.meetu.domain.job.business.dto;

import com.highfive.meetu.domain.application.common.entity.Application;
import com.highfive.meetu.domain.user.common.entity.Profile;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicantResponseDTO {
    private Long applicationId;
    private String applicantName;
    private String email;
    private String phone;

    public static ApplicantResponseDTO fromEntity(Application application) {
        Profile profile = application.getProfile();

        return ApplicantResponseDTO.builder()
            .applicationId(application.getId())
            .applicantName(profile.getAccount().getName())
            .email(profile.getAccount().getEmail())
            .phone(profile.getAccount().getPhone())
            .build();
    }
}
