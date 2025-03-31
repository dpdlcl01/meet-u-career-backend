package com.highfive.meetu.domain.community.common.entity;

import com.highfive.meetu.domain.user.common.entity.Account;
import com.highfive.meetu.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * 게시글 좋아요 엔티티
 *
 * 연관관계:
 * - Account(1) : CommunityLike(N) - CommunityLike가 주인, @JoinColumn 사용
 * - CommunityPost(1) : CommunityLike(N) - CommunityLike가 주인, @JoinColumn 사용
 */
@Entity(name = "communityLike")
@Table(
        indexes = {
                @Index(name = "idx_like_accountId", columnList = "accountId"),
                @Index(name = "idx_like_postId", columnList = "postId")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_account_post", columnNames = {"accountId", "postId"})
        }
)
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"account", "post"})
public class CommunityLike extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accountId", nullable = false)
    private Account account;  // 좋아요를 누른 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId")
    private CommunityPost post;  // 좋아요를 누른 게시글
}