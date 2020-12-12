package com.maxwaterfall.tictactoe.model

import com.maxwaterfall.tictactoe.alias.Board

data class ClientMessage(val type: Type, val data: ClientMessageData?) {
  enum class Type {
    JOIN_GAME,
    MAKE_MOVE
  }
}

sealed class ClientMessageData

data class ClientMakeMoveData(val board: Board) : ClientMessageData()
