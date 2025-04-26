package com.highfive.meetu.domain.community.admin.dto;

import lombok.*;

/**
 * 태그 수정 요청 DTO
 * - 태그 ID, 이름, 상태 정보를 클라이언트로부터 받기 위한 클래스
 */
@Getter
@Setter
public class UpdateTagRequestDTO {
    private Long id;       // 수정할 태그 ID
    private String name;   // 수정할 태그명
    private int status;    // 수정할 상태 (0: 활성, 1: 비활성)
}