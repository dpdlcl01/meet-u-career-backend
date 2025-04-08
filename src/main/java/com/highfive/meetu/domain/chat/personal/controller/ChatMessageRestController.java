package com.highfive.meetu.domain.chat.personal.controller;

import com.highfive.meetu.domain.chat.personal.dto.ChatMessageDTO;

import com.highfive.meetu.domain.chat.personal.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatMessageRestController {

  private final ChatMessageService chatMessageService;

  /**
   * 1️⃣ 채팅방 입장 시: 저장된 메시지 목록 불러오기
   */
  @GetMapping("/{roomId}/messages")
  public ResponseEntity<List<ChatMessageDTO>> getMessagesByRoom(@PathVariable Long roomId) {
    List<ChatMessageDTO> messages = chatMessageService.getMessagesByRoom(roomId);
    return ResponseEntity.ok(messages);
  }

  /**
   * 2️⃣ 채팅방 입장 시 읽음 처리
   */
  @PostMapping("/read/{roomId}")
  public ResponseEntity<Void> markAllAsRead(@PathVariable Long roomId,
                                            @AuthenticationPrincipal(expression = "id") Long accountId) {
    chatMessageService.markAllMessagesAsRead(roomId, accountId);
    return ResponseEntity.ok().build();
  }


}
