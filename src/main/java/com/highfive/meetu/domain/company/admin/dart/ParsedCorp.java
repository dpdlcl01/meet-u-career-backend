package com.highfive.meetu.domain.company.admin.dart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DART Open DART API에서 파싱한 기업 코드 정보
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParsedCorp {
  private String corpCode;    // <corp_code> 값
  private String corpName;    // <corp_name> 값
  private String modifyDate;  // <modify_date> 값
}