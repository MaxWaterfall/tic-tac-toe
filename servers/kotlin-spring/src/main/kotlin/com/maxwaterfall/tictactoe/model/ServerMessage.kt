package com.maxwaterfall.tictactoe.model

import com.maxwaterfall.tictactoe.alias.Board
import com.maxwaterfall.tictactoe.enum.Side

data class ServerMessage(val type: Type, val data: Any?) {
    enum class Type {
        START_GAME,
        MAKE_MOVE,
        GAME_OVER
    }
}

data class ServerStartGameData(val side: Side, val turn: Side)
data class ServerMakeMoveData(val board: Board)
data class ServerGameOverData(val board: Board, val winner: Side)