package com.highfive.meetu.domain.community.personal.service;

import com.highfive.meetu.domain.community.common.entity.CommunityTag;
import com.highfive.meetu.domain.community.common.repository.CommunityTagRepository;
import com.highfive.meetu.domain.community.personal.dto.CommunityTagDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 커뮤니티 태그 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class CommunityTagService {

  private final CommunityTagRepository communityTagRepository;

  /**
   * 사용 가능한 커뮤니티 태그 목록 조회
   * @return CommunityTagDTO 리스트
   */
  @Transactional(readOnly = true)
  public List<CommunityTagDTO> getActiveTags() {
    List<CommunityTag> tags = communityTagRepository.findAllByStatus(CommunityTag.Status.ACTIVE);

    return tags.stream()
        .map(tag -> CommunityTagDTO.builder()
            .id(tag.getId())
            .name(tag.getName())
            .status(tag.getStatus())
            .createdAt(tag.getCreatedAt())
            .updatedAt(tag.getUpdatedAt())
            .build()
        )
        .toList();
  }
}

