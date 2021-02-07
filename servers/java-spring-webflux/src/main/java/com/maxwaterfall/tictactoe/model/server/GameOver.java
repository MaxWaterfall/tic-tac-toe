package com.maxwaterfall.tictactoe.model.server;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.maxwaterfall.tictactoe.model.Side;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableGameOver.class)
public interface GameOver extends ServerBody {

  String board();

  Side winner();
}
