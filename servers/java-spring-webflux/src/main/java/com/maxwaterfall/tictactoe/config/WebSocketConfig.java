package com.maxwaterfall.tictactoe.config;

import com.maxwaterfall.tictactoe.GameWebSocketHandler;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

@Configuration
public class WebSocketConfig {

  @Bean
  public HandlerMapping handlerMapping(GameWebSocketHandler webSocketHandler) {
    Map<String, WebSocketHandler> map = new HashMap<>();
    map.put("/ws/v1", webSocketHandler);
    int order = -1; // before annotated controllers

    return new SimpleUrlHandlerMapping(map, order);
  }

  @Bean
  public WebSocketHandlerAdapter handlerAdapter() {
    return new WebSocketHandlerAdapter();
  }
}
