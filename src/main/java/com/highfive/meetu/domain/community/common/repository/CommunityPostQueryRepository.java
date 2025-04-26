package com.highfive.meetu.domain.community.common.repository;

import com.highfive.meetu.domain.community.common.entity.CommunityPost;
import com.highfive.meetu.domain.community.personal.dto.CommunityPostListDTO;

import java.util.List;

/**
 QueryDSL 기반 커뮤니티 게시글 커스텀 쿼리 리포지토리
 정렬 조건이 복잡하거나 동적 쿼리가 필요한 조회 메서드 정의
 */

public interface CommunityPostQueryRepository {

  // 해시태그별 게시글 조회 (최신순 / 중앙 영역)
  List<CommunityPost> findPostsByTagId(Long tagId, int limit);


  // 해시태그별 인기글 조회 (해시태그에 해당하는 인기글 / 오른쪽 영역)
  List<CommunityPost> findPopularPostsByTag(Long tagId, int limit);


  // 전체 인기 게시글 조회 (해시태그별 좋아요 순으로 1개씩 가져와서 태그 중복없이 7개 해시태그에 대한 인기글 보여주기 / 오른쪽 영역)
  List<CommunityPost> findPopularPostOnePerTag(int limit);

  // 게시글 목록 조회 ( 작성자, 해시태그 정보 포함 / 최신순)
  List<CommunityPostListDTO> findPostListWithWriterAndTag(int limit);
}
