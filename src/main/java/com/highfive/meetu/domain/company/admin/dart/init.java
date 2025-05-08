package com.highfive.meetu.domain.company.admin.dart;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class init implements ApplicationRunner {
  private final DartCorpCodeService dartService;
  @Override
  public void run(ApplicationArguments args) {
    dartService.fetchAndSaveCorpList();
  }
}