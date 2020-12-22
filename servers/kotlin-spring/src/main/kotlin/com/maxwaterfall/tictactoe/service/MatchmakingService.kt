package com.maxwaterfall.tictactoe.service

import com.maxwaterfall.tictactoe.interfaces.MessageListener
import com.maxwaterfall.tictactoe.model.dto.client.ClientMessage
import com.maxwaterfall.tictactoe.model.dto.client.ClientMessageType
import java.util.concurrent.ConcurrentLinkedQueue
import org.springframework.stereotype.Service

/** Responsible for matching players into a game. */
@Service
class MatchmakingService(val gameService: GameService) : MessageListener {
  val lobby = ConcurrentLinkedQueue<String>()

  override fun onMessage(id: String, message: ClientMessage) {
    when (message.header.type) {
      ClientMessageType.JOIN_GAME -> joinLobby(id)
      else -> return
    }
  }

  override fun onDisconnect(id: String) {
    lobby -= id
  }

  private fun joinLobby(id: String) {
    val opponent = lobby.poll()
    if (opponent != null) {
      gameService.createGame(id, opponent)
    } else {
      lobby += id
    }
  }
}
