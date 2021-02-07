package com.maxwaterfall.tictactoe.model.client;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableClientHeader.class)
public interface ClientHeader {
  ClientMessageType type();
}
