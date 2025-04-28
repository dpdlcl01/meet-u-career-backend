// ChatController.java
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

  @GetMapping("/rooms")
  public ResultData<List<ChatRoomDTO>> getMyChatRooms() {
    Long accountId = SecurityUtil.getAccountId();
    List<ChatRoomDTO> rooms = chatRoomService.findMyChatRooms(accountId);
    return ResultData.success(rooms.size(), rooms);
  }

  @GetMapping("/rooms/{roomId}/messages")
  public ResultData<List<ChatMessageDTO>> getMessages(@PathVariable Long roomId) {
    List<ChatMessageDTO> list = chatMessageService.getMessagesByRoom(roomId);
    return ResultData.success(list.size(), list);
  }

  @MessageMapping("/chat/send")
  public void sendMessage(ChatMessageDTO dto) {
    chatRoomService.findOrCreateRoom(ChatRoomDTO.fromMessageDTO(dto));

    if ("ENTER".equals(dto.getType())) {
      dto.setMessage(dto.getSenderName() + "님이 입장하셨습니다.");
    } else if ("LEAVE".equals(dto.getType())) {
      dto.setMessage(dto.getSenderName() + "님이 퇴장하셨습니다.");
    } else if ("TALK".equals(dto.getType())) {
      chatMessageService.saveMessage(dto);
    }

    messagingTemplate.convertAndSend("/sub/chat/room/" + dto.getRoomId(), dto);
  }
}