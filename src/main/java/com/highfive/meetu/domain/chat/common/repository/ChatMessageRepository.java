package com.highfive.meetu.domain.chat.common.repository;

import com.highfive.meetu.domain.chat.common.entity.ChatMessage;
import com.highfive.meetu.domain.chat.common.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

  @Query("SELECT m FROM chatMessage m " +
      "WHERE m.chatRoom.id = :roomId " +
      "AND m.sender.id != :accountId " +
      "AND m.isRead = 0")
  List<ChatMessage> findUnreadMessagesByRoomIdAndReceiverId(@Param("roomId") Long roomId,
                                                            @Param("accountId") Long accountId);
  List<ChatMessage> findByChatRoomIdOrderByCreatedAtAsc(Long roomId);

  // 추가적인 조건이 필요하면 여기에 메서드 정의
  // 특정 기업 담당자 + 구직자 조합으로 채팅방 조회
  Optional<ChatRoom> findByBusinessAccountIdAndPersonalAccountId(Long businessId, Long personalId);

  boolean existsByBusinessAccountIdAndPersonalAccountId(Long businessId, Long personalId);
}
