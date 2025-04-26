package com.highfive.meetu.domain.auth.common.service.findid;

import com.highfive.meetu.domain.auth.common.dto.findid.FindIdRequestDTO;
import com.highfive.meetu.domain.auth.common.dto.findid.FindIdResponseDTO;

/**
 * 사용자 아이디 조회 기능을 제공하는 서비스 인터페이스
 */
public interface FindIdService {

  /**
   * 이름과 이메일을 통해 사용자 아이디(userId)를 조회합니다.
   *
   * @param dto FindIdRequestDTO (name, email 포함)
   * @return FindIdResponseDTO with userId
   */
  FindIdResponseDTO findUserId(FindIdRequestDTO dto);
}
