package com.highfive.meetu.domain.application.common.repository;

import com.highfive.meetu.domain.application.business.dto.ApplicationDetailDTO;

import java.util.List;

public interface ApplicationRepositoryCustom {
  List<ApplicationDetailDTO> findApplicationDetails();
}
