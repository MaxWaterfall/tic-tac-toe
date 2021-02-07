package com.maxwaterfall.tictactoe.model.client;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(using = ClientMessageDeserializer.class)
public interface ClientMessage {
  ClientHeader header();

  ClientBody body();
}
