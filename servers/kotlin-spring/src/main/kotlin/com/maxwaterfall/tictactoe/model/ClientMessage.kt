package com.maxwaterfall.tictactoe.model

import com.maxwaterfall.tictactoe.alias.Board

data class ClientMessage(val type: Type, val data: Any?) {
    enum class Type {
        JOIN_GAME,
        MAKE_MOVE
    }
}

data class ClientMakeMoveData(val board: Board)