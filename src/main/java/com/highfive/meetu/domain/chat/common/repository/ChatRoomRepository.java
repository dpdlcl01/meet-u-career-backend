package com.highfive.meetu.domain.chat.common.repository;

import com.highfive.meetu.domain.chat.common.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 🔥 채팅방 관련 DB 액세스 레포지토리
 */
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

  /**
   * 🔥 내 채팅방 목록 조회 (businessAccountId 또는 personalAccountId 둘 중 하나라도 일치하면)
   */
  List<ChatRoom> findByBusinessAccountIdOrPersonalAccountId(Long businessAccountId, Long personalAccountId);

  /**
   * 🔥 비즈니스 계정 ID + 개인 계정 ID로 채팅방 단일 조회
   */
  Optional<ChatRoom> findByBusinessAccountIdAndPersonalAccountId(Long businessAccountId, Long personalAccountId);
}
