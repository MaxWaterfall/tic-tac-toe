package com.maxwaterfall.tictactoe.model.server;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableServerMessage.class)
public interface ServerMessage {
  ServerHeader header();

  ServerBody body();

  static ServerMessage create(ServerMessageType type, ServerBody body) {
    return ImmutableServerMessage.builder()
        .header(ImmutableServerHeader.builder().type(type).build())
        .body(body)
        .build();
  }
}
