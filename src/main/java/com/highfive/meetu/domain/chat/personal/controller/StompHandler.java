package com.highfive.meetu.domain.chat.personal.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
public class StompHandler implements ChannelInterceptor {

  private static final Logger LOGGER = LoggerFactory.getLogger(StompHandler.class);

  @Override
  public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
    StompCommand command = accessor.getCommand();
    String sessionId = accessor.getSessionId();

    if (command != null) {
      switch (command) {
        case CONNECT:
          LOGGER.info("세션 들어옴 => {}", sessionId);
          break;

        case DISCONNECT:
          LOGGER.info("세션 끊음 => {}", sessionId);
          break;

        default:
          break;
      }
    }
  }
}
