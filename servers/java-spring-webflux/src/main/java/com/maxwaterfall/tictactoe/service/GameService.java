package com.maxwaterfall.tictactoe.service;

import com.maxwaterfall.tictactoe.interfaces.MessageReceiver;
import com.maxwaterfall.tictactoe.interfaces.MessageSender;
import com.maxwaterfall.tictactoe.interfaces.PlayerDisconnectListener;
import com.maxwaterfall.tictactoe.model.Game;
import com.maxwaterfall.tictactoe.model.Game.State;
import com.maxwaterfall.tictactoe.model.ImmutableGame;
import com.maxwaterfall.tictactoe.model.Side;
import com.maxwaterfall.tictactoe.model.client.ClientMessage;
import com.maxwaterfall.tictactoe.model.client.ClientMessageType;
import com.maxwaterfall.tictactoe.model.client.MakeMove;
import com.maxwaterfall.tictactoe.model.server.ImmutableGameOver;
import com.maxwaterfall.tictactoe.model.server.ImmutableMakeMove;
import com.maxwaterfall.tictactoe.model.server.ImmutableStartGame;
import com.maxwaterfall.tictactoe.model.server.ServerMessage;
import com.maxwaterfall.tictactoe.model.server.ServerMessageType;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.EmitFailureHandler;
import reactor.core.publisher.Sinks.Many;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Service
public class GameService implements MessageReceiver, MessageSender, PlayerDisconnectListener {

  private static final Logger LOG = LoggerFactory.getLogger(GameService.class);

  private final Map<String, Many<ServerMessage>> messagesToSend;
  private final Map<String, Game> playersToGames;
  /** Only the below thread can access the above data structures. */
  private final Scheduler accessScheduler;

  private final Random random = new Random();

  public GameService() {
    this.messagesToSend = new HashMap<>();
    this.playersToGames = new HashMap<>();
    this.accessScheduler = Schedulers.newSingle("GameServiceResourceThread");
  }

  /** Creates a new game and notifies players they are ready to start. */
  public void startGame(String player1, String player2) {
    String playerX = random.nextBoolean() ? player1 : player2;

    var game =
        ImmutableGame.builder()
            .playerX(playerX)
            .playerO(playerX.equals(player1) ? player2 : player1)
            .nextPlayer(random.nextBoolean() ? player1 : player2)
            .build();

    accessScheduler.schedule(
        () -> {
          playersToGames.put(player1, game);
          playersToGames.put(player2, game);
        });

    this.notifyPlayers(game);
    LOG.info("New game! {}", game);
  }

  private Mono<Void> makeMove(String player, String board) {
    return Mono.just(true)
        .publishOn(accessScheduler)
        .map(ignored -> playersToGames.get(player))
        .publishOn(Schedulers.parallel())
        .map(game -> this.nextGameState(player, board, game))
        .doOnNext(this::notifyPlayers)
        .then();
  }

  private Game nextGameState(String player, String newBoard, Game currentGame) {
    var game =
        ImmutableGame.copyOf(currentGame)
            .withBoard(newBoard)
            .withNextPlayer(currentGame.getOpponent(player));

    if (game.isWinner(player)) {
      LOG.info("Winner! {}", game);
      return game.withState(
          game.playerX().equals(player) ? State.PLAYER_X_WIN : State.PLAYER_O_WIN);
    } else if (game.isDraw()) {
      LOG.info("Draw! {}", game);
      return game.withState(State.DRAW);
    } else {
      LOG.info("Move made! {}", game);
      return game;
    }
  }

  private void notifyPlayers(Game game) {
    if (game.hasEmptyBoard()) {
      notifyStartGame(game);
    } else if (game.state() == State.IN_PROGRESS) {
      notifyMakeMove(game);
    } else if (game.state() == State.DRAW) {
      notifyDraw(game);
    } else if (game.state() == State.PLAYER_X_WIN) {
      notifyWin(game, game.playerX());
      notifyLose(game, game.playerO());
    } else if (game.state() == State.PLAYER_O_WIN) {
      notifyWin(game, game.playerO());
      notifyLose(game, game.playerX());
    }
  }

  private void notifyStartGame(Game game) {
    var bodyX =
        ImmutableStartGame.builder()
            .side(Side.X)
            .turn(game.nextPlayer().equals(game.playerX()) ? Side.X : Side.O)
            .build();
    var bodyO = bodyX.withSide(Side.O);

    var playerXMessage = ServerMessage.create(ServerMessageType.START_GAME, bodyX);
    var playerOMessage = ServerMessage.create(ServerMessageType.START_GAME, bodyO);

    notify(game.playerX(), playerXMessage);
    notify(game.playerO(), playerOMessage);
  }

  private void notify(String player, ServerMessage message) {
    accessScheduler.schedule(
        () -> {
          var messageSender = messagesToSend.get(player);
          if (messageSender == null) {
            LOG.error(
                "Cannot send message to player {} as message sender is null. Message: {}",
                player,
                message);
            cleanup(player);
          } else {
            messageSender.emitNext(message, EmitFailureHandler.FAIL_FAST);
          }
        });
  }

  private void notifyDraw(Game game) {
    var message =
        ServerMessage.create(
            ServerMessageType.GAME_OVER,
            ImmutableGameOver.builder().board(game.board()).winner(Side.NONE).build());

    notify(game.playerX(), message);
    notify(game.playerO(), message);
  }

  private void notifyWin(Game game, String winner) {
    var message =
        ServerMessage.create(
            ServerMessageType.GAME_OVER,
            ImmutableGameOver.builder()
                .board(game.board())
                .winner(game.playerX().equals(winner) ? Side.X : Side.O)
                .build());

    notify(winner, message);
  }

  private void notifyLose(Game game, String loser) {
    var message =
        ServerMessage.create(
            ServerMessageType.GAME_OVER,
            ImmutableGameOver.builder()
                .board(game.board())
                .winner(game.playerX().equals(loser) ? Side.O : Side.X)
                .build());

    notify(loser, message);
  }

  private void notifyMakeMove(Game game) {
    var message =
        ServerMessage.create(
            ServerMessageType.MAKE_MOVE, ImmutableMakeMove.builder().board(game.board()).build());

    notify(game.nextPlayer(), message);
  }

  @Override
  public Mono<Void> receiveMessage(String id, ClientMessage message) {
    if (message.header().type() == ClientMessageType.MAKE_MOVE) {
      return makeMove(id, ((MakeMove) message.body()).board());
    }

    return Mono.empty();
  }

  @Override
  public Flux<ServerMessage> messagesToSend(String id) {
    Many<ServerMessage> messageSink = Sinks.many().unicast().onBackpressureError();
    this.messagesToSend.put(id, messageSink);
    return messageSink.asFlux();
  }

  @Override
  public void playerDisconnected(String id) {
    accessScheduler.schedule(() -> cleanup(id));
  }

  /**
   * Cleans up all resources used by the player and their opponent. Ends any games in progress. This
   * method must be run on the accessScheduler.
   */
  private void cleanup(String player) {
    var game = this.playersToGames.get(player);
    if (game == null) return; // Cleanup has already happened.

    var opponent = game.getOpponent(player);

    this.playersToGames.remove(player);
    this.playersToGames.remove(opponent);

    var messageSender = this.messagesToSend.get(player);
    var messageSenderOpponent = this.messagesToSend.get(opponent);

    if (messageSender != null) {
      messageSender.emitComplete(EmitFailureHandler.FAIL_FAST);
    }

    if (messageSenderOpponent != null) {
      messageSenderOpponent.emitComplete(EmitFailureHandler.FAIL_FAST);
    }

    this.messagesToSend.remove(opponent);
    this.messagesToSend.remove(player);

    LOG.info("Cleaned up player {} and player {}", player, opponent);
  }
}
