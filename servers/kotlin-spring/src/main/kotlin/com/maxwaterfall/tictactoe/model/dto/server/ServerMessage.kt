package com.maxwaterfall.tictactoe.model.dto.server

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.maxwaterfall.tictactoe.alias.Board
import com.maxwaterfall.tictactoe.enum.Side

/**
 * The header, used to extract the message type before parsing the body. Every message must contain
 * this header.
 */
data class ServerMessageHeader(val type: ServerMessageType)

@JsonSerialize(using = ServerMessageSerializer::class)
data class ServerMessage(val header: ServerMessageHeader, val body: ServerMessageBody?)

sealed class ServerMessageBody

enum class ServerMessageType {
  START_GAME,
  MAKE_MOVE,
  GAME_OVER
}

data class StartGame(val side: Side, val turn: Side) : ServerMessageBody()

data class MakeMove(val board: Board) : ServerMessageBody()

data class GameOver(val board: Board, val winner: Side) : ServerMessageBody()

class ServerMessageSerializer : JsonSerializer<ServerMessage>() {
  override fun serialize(
      message: ServerMessage?, gen: JsonGenerator?, provider: SerializerProvider?
  ) {
    gen ?: return
    message ?: return

    gen.writeStartObject()

    // Write the header.
    gen.writeObjectField("header", message.header)

    // Write the body based on message type.
    when (message.header.type) {
      ServerMessageType.START_GAME -> gen.writeObjectField("body", message.body as StartGame)
      ServerMessageType.MAKE_MOVE -> gen.writeObjectField("body", message.body as MakeMove)
      ServerMessageType.GAME_OVER -> gen.writeObjectField("body", message.body as GameOver)
    }

    gen.writeEndObject()
  }
}
