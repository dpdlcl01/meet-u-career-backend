package com.highfive.meetu.domain.community.common.entity;

import com.highfive.meetu.domain.user.common.entity.Account;
import com.highfive.meetu.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

/**
 * 커뮤니티 댓글 엔티티
 *
 * 연관관계:
 * - CommunityPost(1) : CommunityComment(N) - CommunityComment가 주인, @JoinColumn 사용
 * - Account(1) : CommunityComment(N) - CommunityComment가 주인, @JoinColumn 사용
 */
@Entity(name = "communityComment")
@Table(
        indexes = {
                @Index(name = "idx_comment_postId", columnList = "postId"),
                @Index(name = "idx_comment_accountId", columnList = "accountId"),
                @Index(name = "idx_comment_status", columnList = "status"),
                @Index(name = "idx_comment_createdAt", columnList = "createdAt")
        }
)
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"post", "account"})
public class CommunityComment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId", nullable = false)
    private CommunityPost post;  // 댓글이 속한 게시글

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accountId", nullable = false)
    private Account account;  // 댓글 작성자

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;  // 댓글 내용

    @Column(nullable = false)
    private Integer status;  // 댓글 상태 (ACTIVE, DELETED)

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;  // 댓글 수정일

    // 상태 상수 정의
    public static class Status {
        public static final int ACTIVE = 0;   // 정상
        public static final int DELETED = 1;  // 삭제됨
    }
}