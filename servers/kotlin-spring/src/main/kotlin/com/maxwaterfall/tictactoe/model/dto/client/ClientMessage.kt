package com.maxwaterfall.tictactoe.model.dto.client

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.maxwaterfall.tictactoe.alias.Board

/**
 * The header, used to extract the message type before parsing the body. Every message must contain
 * this header.
 */
data class ClientHeader(val type: ClientMessageType)

@JsonDeserialize(using = ClientMessageDeserializer::class)
data class ClientMessage(val header: ClientHeader, val body: ClientMessageBody?)

sealed class ClientMessageBody

data class MakeMove(val board: Board) : ClientMessageBody()

enum class ClientMessageType {
  JOIN_GAME,
  MAKE_MOVE
}

/** Custom deserializer to parse the body based on the type given in the header. */
class ClientMessageDeserializer : JsonDeserializer<ClientMessage>() {
  override fun deserialize(parser: JsonParser?, contect: DeserializationContext?): ClientMessage? {
    val codec = parser?.codec
    val node: JsonNode? = codec?.readTree(parser)

    val headerAsNode = node?.get("header")
    print(headerAsNode?.asText())

    val header = codec?.treeToValue(headerAsNode, ClientHeader::class.java)

    return when (header?.type) {
      ClientMessageType.JOIN_GAME -> ClientMessage(header, null)
      ClientMessageType.MAKE_MOVE ->
          ClientMessage(header, codec.treeToValue(node?.get("body"), MakeMove::class.java))
      else -> null
    }
  }
}
