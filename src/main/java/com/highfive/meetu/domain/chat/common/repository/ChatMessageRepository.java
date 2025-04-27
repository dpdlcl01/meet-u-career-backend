package com.highfive.meetu.domain.chat.common.repository;

import com.highfive.meetu.domain.chat.common.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

  @Query("SELECT m FROM chatMessage m " +
      "WHERE m.chatRoom.id = :roomId " +
      "AND m.sender.id != :accountId " +
      "AND m.isRead = 0")
  List<ChatMessage> findUnreadMessagesByRoomIdAndReceiverId(@Param("roomId") Long roomId,
                                                            @Param("accountId") Long accountId);

  List<ChatMessage> findByChatRoomIdOrderByCreatedAtAsc(Long roomId);

  @Query("SELECT m FROM chatMessage m WHERE m.chatRoom.id = :roomId ORDER BY m.createdAt DESC")
  Optional<ChatMessage> findTopByChatRoomIdOrderByCreatedAtDesc(@Param("roomId") Long roomId);

  int countByChatRoomIdAndIsReadAndSenderIdNot(Long roomId, int isRead, Long senderId);
}
