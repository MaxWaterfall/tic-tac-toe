package com.maxwaterfall.tictactoe.model.client;

public interface ClientBody {
  final class EmptyBody implements ClientBody {}

  ClientBody EMPTY_BODY = new EmptyBody();
}
