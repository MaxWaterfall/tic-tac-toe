package com.maxwaterfall.tictactoe.model.server;

public interface ServerBody {

  class EmptyBody implements ServerBody {}

  ServerBody EMPTY_BODY = new EmptyBody();
}
