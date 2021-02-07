package com.maxwaterfall.tictactoe.model.server;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableMakeMove.class)
public interface MakeMove extends ServerBody {

  String board();
}
