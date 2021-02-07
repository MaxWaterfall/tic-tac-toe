package com.maxwaterfall.tictactoe.interfaces;

import com.maxwaterfall.tictactoe.model.client.ClientMessage;
import reactor.core.publisher.Mono;

public interface MessageReceiver {
  Mono<Void> receiveMessage(String id, ClientMessage message);
}
