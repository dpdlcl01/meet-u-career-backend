package com.highfive.meetu.domain.chat.personal.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StompHandler implements ChannelInterceptor {
  @Override
  public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
    StompHeaderAccessor a = StompHeaderAccessor.wrap(message);
    if (StompCommand.CONNECT.equals(a.getCommand())) {
      log.info("STOMP Connect: {}", a.getSessionId());
    } else if (StompCommand.DISCONNECT.equals(a.getCommand())) {
      log.info("STOMP Disconnect: {}", a.getSessionId());
    }
  }
} 