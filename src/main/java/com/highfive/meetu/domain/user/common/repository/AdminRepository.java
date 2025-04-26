package com.highfive.meetu.domain.user.common.repository;

import com.highfive.meetu.domain.user.common.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Admin (관리자 계정) Repository
 * - 관리자 로그인 및 계정 조회에 사용
 */
@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    /**
     * 관리자 이메일로 조회
     * - 로그인 시 사용
     *
     * @param email 관리자 이메일 (고유 ID)
     * @return Optional<Admin>
     */
    Optional<Admin> findByEmail(String email);
}
