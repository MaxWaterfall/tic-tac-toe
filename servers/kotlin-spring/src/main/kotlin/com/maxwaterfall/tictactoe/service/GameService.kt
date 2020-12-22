package com.maxwaterfall.tictactoe.service

import com.maxwaterfall.tictactoe.alias.Board
import com.maxwaterfall.tictactoe.enum.Side
import com.maxwaterfall.tictactoe.exception.GameException
import com.maxwaterfall.tictactoe.interfaces.MessageListener
import com.maxwaterfall.tictactoe.logger.LoggerDelegate
import com.maxwaterfall.tictactoe.model.*
import com.maxwaterfall.tictactoe.model.dto.client.ClientMessage
import com.maxwaterfall.tictactoe.model.dto.client.MakeMove
import com.maxwaterfall.tictactoe.model.dto.server.*
import com.maxwaterfall.tictactoe.model.dto.server.MakeMove as ServerMakeMove
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random
import org.springframework.stereotype.Service

/**
 * Responsible for managing the game. This includes:
 * - creating, updating and ending games.
 * - validating moves.
 */
@Service
class GameService : MessageListener {

  private val playerIdsToSendFunctions = ConcurrentHashMap<String, (ServerMessage) -> Unit>()
  private val playerIdsToGames = ConcurrentHashMap<String, Game>()

  companion object {
    private val log by LoggerDelegate()
  }

  override fun onConnect(id: String, sendMessage: (ServerMessage) -> Unit) {
    playerIdsToSendFunctions[id] = sendMessage
  }

  override fun onMessage(id: String, message: ClientMessage) {
    when (message.body) {
      is MakeMove -> {
        makeMove(id, message.body.board)
      }
    }
  }

  override fun onDisconnect(id: String) {
    playerIdsToSendFunctions -= id
    val game = playerIdsToGames[id]

    game ?: return

    playerIdsToGames -= game.playerO
    playerIdsToGames -= game.playerX
  }

  /** Creates a new game and updates the players. */
  fun createGame(player1Id: String, player2Id: String) {
    val player1IsX = Random.nextBoolean()

    val game =
        Game(
            id = UUID.randomUUID().toString(),
            playerX = if (player1IsX) player1Id else player2Id,
            playerO = if (player1IsX) player2Id else player1Id,
            nextPlayer = if (Random.nextBoolean()) player1Id else player2Id)

    playerIdsToGames[player1Id] = game
    playerIdsToGames[player2Id] = game

    sendStartGameMessage(game)
  }

  /** Sends the given [ServerMessage] to the player. */
  private fun sendMessage(id: String, message: ServerMessage) {
    val sendFunction =
        playerIdsToSendFunctions[id] ?: throw GameException("Send function was null for id $id")
    sendFunction(message)
  }

  /** Sends a START_GAME message to the given player. */
  private fun sendStartGameMessage(game: Game) {
    val turn = if (game.playerX == game.nextPlayer) Side.X else Side.O

    sendMessage(
        game.playerX,
        ServerMessage(ServerMessageHeader(ServerMessageType.START_GAME), StartGame(Side.X, turn)))

    sendMessage(
        game.playerO,
        ServerMessage(ServerMessageHeader(ServerMessageType.START_GAME), StartGame(Side.O, turn)))
  }

  /** Sends a GAME_OVER message to both players in the game. */
  private fun sendGameOverMessage(game: Game, winner: Side) {
    val message =
        ServerMessage(
            ServerMessageHeader(ServerMessageType.GAME_OVER), GameOver(game.board, winner))

    sendMessage(game.playerX, message)
    sendMessage(game.playerO, message)
  }

  /** Sends a MAKE_MOVE to the player with the given id. */
  private fun sendMakeMoveMessage(id: String, board: Board) {
    sendMessage(
        id, ServerMessage(ServerMessageHeader(ServerMessageType.MAKE_MOVE), ServerMakeMove(board)))
  }

  /** Calculates the state of the game after applying the given board and updates the players. */
  private fun makeMove(id: String, board: Board) {
    val game =
        playerIdsToGames[id]
            ?: throw GameException("Player $id tried to make a move when they are not in a game")

    val playerSide = if (game.playerX == id) Side.X else Side.O

    // TODO: Don't blindly trust the client.
    when (getGameState(game.board, playerSide)) {
      GameState.WIN -> sendGameOverMessage(game, playerSide)
      GameState.DRAW -> sendGameOverMessage(game, Side.NONE)
      GameState.IN_PROGRESS -> {
        game.board = board
        game.nextPlayer = if (id == game.playerX) game.playerO else game.playerX
        sendMakeMoveMessage(game.nextPlayer, game.board)
      }
    }

    log.info("Player {} made move [{}]", id, board)
  }

  /**
   * Calculates and returns the state of the game. Assumes the move has already been validated. The
   * given side must not be [Side.NONE].
   */
  private fun getGameState(board: Board, side: Side): GameState {
    return side.toString()[0]
        .let { sideAsChar ->
          board.map {
            // Map the board to a new board where all this players moves are 1 and anything else is
            // 0.
            if (it == sideAsChar) 1 else 0
          }
        }
        .let { numbers ->
          // If any row, col or diagonal equals 3 the player has won.
          when (3) {
            // Rows.
            numbers[0] + numbers[1] + numbers[2] -> GameState.WIN
            numbers[3] + numbers[4] + numbers[5] -> GameState.WIN
            numbers[6] + numbers[7] + numbers[8] -> GameState.WIN
            // Columns.
            numbers[0] + numbers[3] + numbers[6] -> GameState.WIN
            numbers[1] + numbers[4] + numbers[7] -> GameState.WIN
            numbers[2] + numbers[5] + numbers[8] -> GameState.WIN
            // Diagonals.
            numbers[0] + numbers[4] + numbers[8] -> GameState.WIN
            numbers[2] + numbers[4] + numbers[6] -> GameState.WIN

            // No winner, check if the game is still in progress or is a draw.
            else -> if (board.contains(' ')) GameState.IN_PROGRESS else GameState.DRAW
          }
        }
  }

  enum class GameState {
    WIN,
    DRAW,
    IN_PROGRESS
  }
}
