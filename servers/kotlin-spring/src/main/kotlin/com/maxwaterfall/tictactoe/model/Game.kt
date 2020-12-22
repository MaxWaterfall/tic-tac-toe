package com.maxwaterfall.tictactoe.model

import com.maxwaterfall.tictactoe.alias.Board

data class Game(
    val id: String,
    val playerX: String,
    val playerO: String,
    var nextPlayer: String,
    var board: Board = "")
