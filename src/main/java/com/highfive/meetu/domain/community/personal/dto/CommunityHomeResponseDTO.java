package com.highfive.meetu.domain.community.personal.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityHomeResponseDTO {
  private List<CommunityNewsDTO> news;
  private List<CommunityPostDTO> posts;
}
