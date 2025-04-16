package com.highfive.meetu.domain.chat.common.repository;

import com.highfive.meetu.domain.chat.common.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

  // 채용담당자 ID + 구직자 ID로 채팅방 존재 여부 및 조회
  boolean existsByBusinessAccountIdAndPersonalAccountId(Long businessAccountId, Long personalAccountId);

  Optional<ChatRoom> findByBusinessAccountIdAndPersonalAccountId(Long businessAccountId, Long personalAccountId);
}
