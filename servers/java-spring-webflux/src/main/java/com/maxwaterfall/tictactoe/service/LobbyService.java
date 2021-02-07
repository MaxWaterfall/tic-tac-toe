package com.maxwaterfall.tictactoe.service;

import com.maxwaterfall.tictactoe.interfaces.MessageReceiver;
import com.maxwaterfall.tictactoe.interfaces.PlayerDisconnectListener;
import com.maxwaterfall.tictactoe.model.client.ClientMessage;
import com.maxwaterfall.tictactoe.model.client.ClientMessageType;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class LobbyService implements MessageReceiver, PlayerDisconnectListener {

  private static final Logger LOG = LoggerFactory.getLogger(LobbyService.class);

  private final Queue<String> lobby;
  private final GameService gameService;

  public LobbyService(GameService gameService) {
    this.lobby = new ConcurrentLinkedQueue<>();
    this.gameService = gameService;
  }

  /** Adds a player to the lobby or starts a game if a player is waiting. */
  private void joinLobby(String id) {
    var otherPlayer = lobby.poll();
    if (otherPlayer != null) {
      this.gameService.startGame(id, otherPlayer);
    }

    lobby.add(id);
  }

  @Override
  public Mono<Void> receiveMessage(String id, ClientMessage message) {
    if (message.header().type() == ClientMessageType.JOIN_GAME) {
      joinLobby(id);
    }

    return Mono.empty();
  }

  @Override
  public void playerDisconnected(String id) {
    this.lobby.remove(id);
  }
}
