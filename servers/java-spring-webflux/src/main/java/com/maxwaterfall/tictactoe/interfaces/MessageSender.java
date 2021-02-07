package com.maxwaterfall.tictactoe.interfaces;

import com.maxwaterfall.tictactoe.model.server.ServerMessage;
import reactor.core.publisher.Flux;

public interface MessageSender {
  Flux<ServerMessage> messagesToSend(String id);
}
