package com.highfive.meetu.domain.user.common.entity;

import com.highfive.meetu.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * 관리자 엔티티
 */
@Entity(name = "admin")
@Table(
        indexes = {
                @Index(name = "idx_admin_email", columnList = "email"),
                @Index(name = "idx_admin_role", columnList = "role")
        }
)
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Admin extends BaseEntity {
    @Column(length = 255, nullable = false, unique = true)
    private String email;  // 관리자 이메일 (로그인 ID)

    @Column(length = 255, nullable = false)
    private String password;  // 비밀번호 (해시 저장)

    @Column(length = 50, nullable = false)
    private String name;  // 관리자 이름

    @Column(nullable = false)
    private Integer role;  // 관리자 역할 (SUPER, ADMIN)

    /**
     * 관리자 권한 등급
     */
    public static class Role {
        public static final int SUPER = 1;  // 슈퍼 관리자 (모든 권한)
        public static final int ADMIN = 2;  // 일반 관리자 (일부 제한 가능)
    }

}