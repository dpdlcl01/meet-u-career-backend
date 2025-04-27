package com.highfive.meetu.domain.chat.personal.controller;

import com.highfive.meetu.domain.chat.personal.dto.ChatMessageDTO;
import com.highfive.meetu.domain.chat.personal.dto.ChatRoomDTO;
import com.highfive.meetu.domain.chat.personal.service.ChatMessageService;
import com.highfive.meetu.domain.chat.personal.service.ChatRoomService;
import com.highfive.meetu.global.common.response.ResultData;
import com.highfive.meetu.infra.oauth.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

  private final ChatRoomService chatRoomService;
  private final ChatMessageService chatMessageService;
  private final SimpMessageSendingOperations messagingTemplate;

  /**
   * 🔥 내 채팅방 목록 조회 (accountId 기준)
   */
  @GetMapping("/rooms")
  public ResultData<List<ChatRoomDTO>> getMyChatRooms() {
    Long accountId = SecurityUtil.getAccountId();
    List<ChatRoomDTO> rooms = chatRoomService.findMyChatRooms(accountId);
    return ResultData.success(rooms.size(), rooms);
  }

  /**
   * 🔥 클라이언트로부터 채팅 메시지를 수신하면 채팅방 생성 및 메시지 처리
   */
  @MessageMapping("/chat/send")
  public void sendMessage(ChatMessageDTO dto) {
    // ✅ 채팅방이 존재하지 않으면 생성
    ChatRoomDTO chatRoomDTO = ChatRoomDTO.fromMessageDTO(dto);
    chatRoomService.findOrCreateRoom(chatRoomDTO);

    // 💬 메시지 타입 분기 처리
    switch (dto.getType()) {
      case "ENTER":
        dto.setMessage(dto.getSenderName() + "님이 입장하셨습니다.");
        break;
      case "LEAVE":
        dto.setMessage(dto.getSenderName() + "님이 퇴장하셨습니다.");
        break;
      case "TALK":
        chatMessageService.saveMessage(dto);
        break;
    }

    // 📤 채팅방 구독자들에게 메시지 전송
    messagingTemplate.convertAndSend("/sub/chat/" + dto.getRoomId(), dto);
  }
}