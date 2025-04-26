package com.highfive.meetu.domain.dashboard.personal.controller;

import com.highfive.meetu.domain.dashboard.personal.dto.ProfileInfoDTO;
import com.highfive.meetu.domain.dashboard.personal.service.ProfileService;
import com.highfive.meetu.global.common.response.ResultData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/personal/profile")
@RequiredArgsConstructor
public class ProfileController {

  private final ProfileService profileService;

  @GetMapping("/me")
  public ResultData<ProfileInfoDTO> getProfileInfo(@RequestParam Long profileId) {
    return ResultData.success(1, profileService.getProfileInfo(profileId));
  }

  @PutMapping("/me")
  public ResultData<Void> updateProfile(@RequestParam Long profileId, @RequestBody ProfileInfoDTO dto) {
    profileService.updateProfileByProfileId(profileId, dto);
    return ResultData.success(1, null);
  }
}
