package com.highfive.meetu.domain.application.business.controller;

import com.highfive.meetu.domain.application.business.dto.ApplicationDetailDTO;
import com.highfive.meetu.domain.application.business.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ApplicationController {

  private final ApplicationService applicationService;

  @GetMapping("/details")
  public ResponseEntity<List<ApplicationDetailDTO>> getApplicationDetails() {
    return ResponseEntity.ok(applicationService.getApplications());
  }
}
