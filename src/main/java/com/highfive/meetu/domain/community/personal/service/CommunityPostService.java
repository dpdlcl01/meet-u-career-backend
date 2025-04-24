package com.highfive.meetu.domain.community.personal.service;

import com.highfive.meetu.domain.community.common.entity.CommunityPost;
import com.highfive.meetu.domain.community.common.entity.CommunityTag;
import com.highfive.meetu.domain.community.common.repository.CommunityPostQueryRepository;
import com.highfive.meetu.domain.community.common.repository.CommunityPostRepository;
import com.highfive.meetu.domain.community.common.repository.CommunityTagRepository;
import com.highfive.meetu.domain.community.personal.dto.CommunityPostDTO;
import com.highfive.meetu.domain.community.personal.dto.CommunityPostListDTO;
import com.highfive.meetu.domain.community.personal.dto.CommunityPostSimpleDTO;
import com.highfive.meetu.domain.user.common.entity.Account;
import com.highfive.meetu.domain.user.common.repository.AccountRepository;
import com.highfive.meetu.global.common.exception.BadRequestException;
import com.highfive.meetu.global.common.exception.NotFoundException;
import com.highfive.meetu.infra.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 커뮤니티 게시글 서비스
 */
@Service
@RequiredArgsConstructor
public class CommunityPostService {

  private final CommunityPostRepository communityPostRepository;
  private final CommunityTagRepository communityTagRepository;
  private final AccountRepository accountRepository;
  private final CommunityPostQueryRepository communityPostQueryRepository;
  private final S3Service s3Service;

  // 게시글 작성
  @Transactional
  public CommunityPostDTO createPost(CommunityPostDTO dto, MultipartFile image) {
    if (dto.getTagId() == null) {
      throw new BadRequestException("해시태그는 반드시 선택해야 합니다.");
    }

    Account account = accountRepository.findById(dto.getAccountId())
        .orElseThrow(() -> new NotFoundException("작성자 정보를 찾을 수 없습니다."));

    CommunityTag tag = communityTagRepository.findById(dto.getTagId())
        .orElseThrow(() -> new NotFoundException("해시태그 정보를 찾을 수 없습니다."));

    String uploadedKey = null;
    if (image != null && !image.isEmpty()) {
      uploadedKey = s3Service.uploadFile(image, "community");
    }

    CommunityPost post = CommunityPost.builder()
        .account(account)
        .tag(tag)
        .title(dto.getTitle())
        .content(dto.getContent())
        .postImageKey(uploadedKey)
        .likeCount(0)
        .commentCount(0)
        .status(CommunityPost.Status.ACTIVE)
        .build();

    communityPostRepository.save(post);
    return convertToDTO(post);
  }


  // 전체 게시글 조회 (최신순 / 중앙영역)
  @Transactional(readOnly = true)
  public List<CommunityPostDTO> getAllActivePosts() {
    return communityPostRepository.findAllByStatusOrderByCreatedAtDesc(CommunityPost.Status.ACTIVE)
        .stream().map(this::convertToDTO).toList();
  }

  // 전체 게시글 조회 (최신순 / 중앙영역 / 단건이 아닌 리스트 조회용)
  @Transactional(readOnly = true)
  public List<CommunityPostListDTO> getPostListWithWriterAndTag(int limit) {
    return communityPostQueryRepository.findPostListWithWriterAndTag(limit).stream()
        .map(dto -> new CommunityPostListDTO(
            dto.getPostId(),
            dto.getTitle(),
            dto.getContent(),
            dto.getTagId(),
            dto.getTagName(),
            dto.getAccountId(),
            dto.getName(), // 작성자 이름 그대로 사용
            s3Service.getImageUrl(dto.getPostImageUrl()), // S3 Presigned URL 변환
            dto.getLikeCount(),
            dto.getCommentCount(),
            dto.getCreatedAt(),
            dto.getProfileImageUrl()
        ))
        .toList();
  }


  // 전체 인기 게시글 조회 (해시태그별로 1개씩 뽑아서 조회 / 오른쪽 영역)
  @Transactional(readOnly = true)
  public List<CommunityPostSimpleDTO> getPopularPosts(int limit) {
    return communityPostQueryRepository.findPopularPostOnePerTag(limit)
        .stream()
        .map(this::convertToSimpleDTO)
        .toList();
  }

  // 해시태그별 인기 게시글 조회 (좋아요 순 / 오른쪽 인기글 영역)
  @Transactional(readOnly = true)
  public List<CommunityPostSimpleDTO> getPopularPostsByTag(Long tagId, int limit) {
    return communityPostQueryRepository.findPopularPostsByTag(tagId, limit)
        .stream()
        .map(this::convertToSimpleDTO)
        .toList();
  }

  // 내가 쓴 게시글 조회 (최신순)
  @Transactional(readOnly = true)
  public List<CommunityPostDTO> getMyPosts(Long accountId) {
    return communityPostRepository
        .findAllByAccountIdAndStatusOrderByCreatedAtDesc(accountId, CommunityPost.Status.ACTIVE)
        .stream().map(this::convertToDTO).toList();
  }

  // 게시글 상세 조회 (댓글과 함께 전체 보이기)
  @Transactional(readOnly = true)
  public CommunityPostDTO getPostDetail(Long postId) {
    CommunityPost post = communityPostRepository.findById(postId)
        .filter(p -> p.getStatus() == CommunityPost.Status.ACTIVE)
        .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다."));

    return convertToDTO(post);
  }

  // 해시태그별 게시글 조회 (최신순 / 중앙영역)
  @Transactional(readOnly = true)
  public List<CommunityPostDTO> getPostsByTagId(Long tagId) {
    return communityPostRepository
        .findAllByTagIdAndStatusOrderByCreatedAtDesc(tagId, CommunityPost.Status.ACTIVE)
        .stream().map(this::convertToDTO).toList();
  }

  // 해시태그별 게시글 조회 (최신순 / QueryDSL 활용)
  @Transactional(readOnly = true)
  public List<CommunityPostDTO> getPostsByTagQueryDSL(Long tagId, int limit) {
    return communityPostQueryRepository.findPostsByTagId(tagId, limit)
        .stream().map(this::convertToDTO).toList();
  }

  // 게시글 수정
  @Transactional
  public CommunityPostDTO updatePost(CommunityPostDTO dto, MultipartFile image) {
    CommunityPost post = communityPostRepository.findById(dto.getId())
        .filter(p -> p.getStatus() == CommunityPost.Status.ACTIVE)
        .orElseThrow(() -> new NotFoundException("수정할 게시글이 존재하지 않습니다."));

    if (!post.getAccount().getId().equals(dto.getAccountId())) {
      throw new BadRequestException("게시글 수정 권한이 없습니다.");
    }

    CommunityTag tag = communityTagRepository.findById(dto.getTagId())
        .orElseThrow(() -> new NotFoundException("해시태그 정보를 찾을 수 없습니다."));

    post.setTitle(dto.getTitle());
    post.setContent(dto.getContent());
    post.setTag(tag);

    if (image != null && !image.isEmpty()) {
      // 새 이미지 업로드했을 때만 새로 저장
      String uploadedKey = s3Service.uploadFile(image, "community");
      post.setPostImageKey(uploadedKey);
    } else if (dto.getExistingImageUrl() != null && !dto.getExistingImageUrl().isBlank()) {
      // 기존 이미지 Presigned URL로부터 S3 Key 추출
      String existingKey = extractKeyFromUrl(dto.getExistingImageUrl());
      post.setPostImageKey(existingKey);
    } else {
      // 기존 postImageKey가 있었으면 유지해야 하고, 없으면 null
      if (post.getPostImageKey() != null) {
        // 기존 이미지 유지
        post.setPostImageKey(post.getPostImageKey());
      } else {
        // 원래도 이미지 없던 글이면 null
        post.setPostImageKey(null);
      }
    }

    return convertToDTO(post);
  }


  private String extractKeyFromUrl(String url) {
    if (url == null) return null;

    // 이미 키 형태라면 그대로 반환
    if (!url.startsWith("http")) {
      return url;
    }

    try {
      // URL에서 경로 추출
      java.net.URL parsedUrl = new java.net.URL(url);
      String path = parsedUrl.getPath();

      // 형식: /bucket-name/community/image.jpg
      // community/image.jpg만 필요
      String[] pathParts = path.split("/");
      if (pathParts.length >= 3) {
        // 경로에서 버킷 이름 이후 부분만 사용
        return String.join("/", java.util.Arrays.copyOfRange(pathParts, 2, pathParts.length));
      }

      return path.startsWith("/") ? path.substring(1) : path;
    } catch (Exception e) {
      System.err.println("URL 파싱 오류: " + e.getMessage() + " for URL: " + url);
      // 파싱 실패 시 null 대신 원래 URL 반환 (안전 조치)
      return url;
    }
  }



  // 게시글 삭제 (Soft Delete 방식)
  @Transactional
  public void deletePost(Long postId, Long accountId) {
    CommunityPost post = communityPostRepository.findById(postId)
        .filter(p -> p.getStatus() == CommunityPost.Status.ACTIVE)
        .orElseThrow(() -> new NotFoundException("삭제할 게시글이 존재하지 않습니다."));

    // 본인 확인
    if (!post.getAccount().getId().equals(accountId)) {
      throw new BadRequestException("게시글 삭제 권한이 없습니다.");
    }

    post.setStatus(CommunityPost.Status.DELETED);
  }

  // Entity → DTO 변환 + Presigned URL 변환 포함
  private CommunityPostDTO convertToDTO(CommunityPost post) {
    // 게시글 이미지 Presigned URL 생성
    String postImageUrl = null;
    if (post.getPostImageKey() != null) {
      postImageUrl = s3Service.generatePresignedUrl(post.getPostImageKey());
    }

    // 작성자 프로필 이미지 Presigned URL 생성
    String profileImageUrl = null;
    if (post.getAccount() != null && post.getAccount().getProfile() != null && post.getAccount().getProfile().getProfileImageKey() != null) {
      profileImageUrl = s3Service.generatePresignedUrl(post.getAccount().getProfile().getProfileImageKey());
    }

    return CommunityPostDTO.builder()
        .id(post.getId())
        .accountId(post.getAccount().getId())
        .tagId(post.getTag().getId())
        .title(post.getTitle())
        .content(post.getContent())
        .postImageKey(post.getPostImageKey())
        .postImageUrl(postImageUrl)
        .profileImageUrl(profileImageUrl)
        .likeCount(post.getLikeCount())
        .commentCount(post.getCommentCount())
        .status(post.getStatus())
        .createdAt(post.getCreatedAt())
        .updatedAt(post.getUpdatedAt())
        .build();
  }



  // 인기글 조회시 간단한 목록으로만 보여주기 위한 메서드
  private CommunityPostSimpleDTO convertToSimpleDTO(CommunityPost post) {
    return CommunityPostSimpleDTO.builder()
        .id(post.getId())
        .title(post.getTitle())
        .likeCount(post.getLikeCount())
        .commentCount(post.getCommentCount())
        .createdAt(post.getCreatedAt())
        .build();
  }

}
