package com.highfive.meetu.domain.chat.common.repository;

import com.highfive.meetu.domain.chat.common.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

  // ✅ 채팅방 ID로 메시지들 createdAt 순으로 가져오기
  @Query("SELECT m FROM chatMessage m WHERE m.chatRoom.id = :roomId ORDER BY m.createdAt ASC")
  List<ChatMessage> findByChatRoomIdOrderByCreatedAt(Long roomId);
}
