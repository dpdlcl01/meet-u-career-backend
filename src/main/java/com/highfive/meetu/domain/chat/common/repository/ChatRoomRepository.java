package com.highfive.meetu.domain.chat.common.repository;

import com.highfive.meetu.domain.chat.common.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

  // 🔥 accountId가 포함된 내 채팅방 목록 조회
  @Query("""
        select cr
        from chatRoom cr
        where cr.businessAccount.id = :accountId
           or cr.personalAccount.id = :accountId
        order by cr.updatedAt desc
    """)
  List<ChatRoom> findMyRooms(@Param("accountId") Long accountId);

  // 🔥 특정 조건에 맞는 채팅방 하나 조회
  @Query("""
        select cr
        from chatRoom cr
        where cr.company.id = :companyId
          and cr.businessAccount.id = :businessAccountId
          and cr.personalAccount.id = :personalAccountId
          and (:resumeId is null or cr.resume.id = :resumeId)
    """)
  Optional<ChatRoom> findRoom(
      @Param("companyId") Long companyId,
      @Param("businessAccountId") Long businessAccountId,
      @Param("personalAccountId") Long personalAccountId,
      @Param("resumeId") Long resumeId
  );
}
