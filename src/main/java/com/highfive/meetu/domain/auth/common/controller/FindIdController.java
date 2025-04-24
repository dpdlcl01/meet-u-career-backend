package com.highfive.meetu.domain.auth.common.controller;

import com.highfive.meetu.domain.auth.common.dto.findid.FindIdRequestDTO;
import com.highfive.meetu.domain.auth.common.dto.findid.FindIdResponseDTO;
import com.highfive.meetu.domain.auth.common.service.findid.FindIdService;
import com.highfive.meetu.global.common.response.ResultData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 아이디 조회 API 컨트롤러
 */
@RestController
@RequestMapping("/api/find-id")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000") // <-- Add this line!
public class FindIdController {

  private final FindIdService findIdService;

  /**
   * 사용자 이름과 이메일을 입력받아 userId를 조회합니다.
   *
   * @param dto FindIdRequestDTO: name, email 포함
   * @return ResultData<FindIdResponseDTO> 반환 (userId 포함)
   */
  @PostMapping
  public ResultData<FindIdResponseDTO> findUserId(@RequestBody FindIdRequestDTO dto) {
    FindIdResponseDTO response = findIdService.findUserId(dto);
    return ResultData.success(1, response);
  }
}