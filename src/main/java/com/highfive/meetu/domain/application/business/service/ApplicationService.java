package com.highfive.meetu.domain.application.business.service;

import com.highfive.meetu.domain.application.business.dto.ApplicationDetailDTO;
import com.highfive.meetu.domain.application.common.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {

  private final ApplicationRepository applicationRepository;

  public List<ApplicationDetailDTO> getApplications() {
    return applicationRepository.findApplicationDetails();
  }
}
