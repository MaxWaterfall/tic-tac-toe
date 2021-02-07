package com.maxwaterfall.tictactoe.model.client;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableMakeMove.class)
public interface MakeMove extends ClientBody {
  String board();
}
