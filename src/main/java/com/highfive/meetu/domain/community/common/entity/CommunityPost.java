package com.highfive.meetu.domain.community.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.highfive.meetu.domain.user.common.entity.Account;
import com.highfive.meetu.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 커뮤니티 게시글 엔티티
 *
 * 연관관계:
 * - Account(1) : CommunityPost(N) - CommunityPost가 주인, @JoinColumn 사용
 * - CommunityTag(1) : CommunityPost(N) - CommunityPost가 주인, @JoinColumn 사용
 * - CommunityPost(1) : CommunityComment(N) - CommunityPost가 비주인, mappedBy 사용
 * - CommunityPost(1) : CommunityLike(N) - CommunityPost가 비주인, mappedBy 사용
 */
@Entity(name = "communityPost")
@Table(
        indexes = {
                @Index(name = "idx_post_accountId", columnList = "accountId"),
                @Index(name = "idx_post_tagId", columnList = "tagId"),
                @Index(name = "idx_post_status", columnList = "status"),
                @Index(name = "idx_post_createdAt", columnList = "createdAt")
        }
)
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"account", "tag", "commentList", "likeList"})
public class CommunityPost extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accountId", nullable = false)
    private Account account;  // 게시글 작성자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tagId", nullable = false)
    private CommunityTag tag;  // 게시글의 태그

    @Column(length = 255, nullable = false)
    private String title;  // 게시글 제목

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;  // 게시글 내용

    @Column(length = 500)
    private String postImageKey;  // S3에 저장된 대표 이미지 파일의 Key (예: communityPost/post123_abc.jpg)

    @Column(nullable = false)
    private Integer likeCount = 0;  // 좋아요 수

    @Column(nullable = false)
    private Integer commentCount = 0;  // 댓글 수

    @Column(nullable = false)
    private Integer status;  // 게시글 상태 (ACTIVE, DELETED)

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;  // 게시글 수정일

    @BatchSize(size = 20)
    @OneToMany(
            mappedBy = "post",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @JsonIgnoreProperties({"post"})
    @Builder.Default
    private List<CommunityComment> commentList = new ArrayList<>();

    @BatchSize(size = 20)
    @OneToMany(
            mappedBy = "post",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @JsonIgnoreProperties({"post"})
    @Builder.Default
    private List<CommunityLike> likeList = new ArrayList<>();

    // 상태 상수 정의
    public static class Status {
        public static final int ACTIVE = 0;   // 게시 중
        public static final int DELETED = 1;  // 삭제됨
    }
}