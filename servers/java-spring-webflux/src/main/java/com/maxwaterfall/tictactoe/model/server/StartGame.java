package com.maxwaterfall.tictactoe.model.server;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.maxwaterfall.tictactoe.model.Side;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableStartGame.class)
public interface StartGame extends ServerBody {
  Side side();

  Side turn();
}
