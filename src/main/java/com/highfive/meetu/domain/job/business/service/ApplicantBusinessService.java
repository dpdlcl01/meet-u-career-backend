package com.highfive.meetu.domain.job.business.service;

import com.highfive.meetu.domain.application.common.repository.ApplicationRepository;
import com.highfive.meetu.domain.job.business.dto.ApplicantResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicantBusinessService {

    private final ApplicationRepository applicationRepository;

    public List<ApplicantResponseDTO> getApplicantsByJobPosting(Long jobPostingId) {
        return applicationRepository.findAllByJobPostingId(jobPostingId)
            .stream()
            .map(ApplicantResponseDTO::fromEntity)
            .toList();
    }
}
