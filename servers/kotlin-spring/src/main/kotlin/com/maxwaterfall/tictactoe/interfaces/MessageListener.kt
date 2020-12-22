package com.maxwaterfall.tictactoe.interfaces

import com.maxwaterfall.tictactoe.model.dto.client.ClientMessage
import com.maxwaterfall.tictactoe.model.dto.server.ServerMessage

interface MessageListener {
  fun onConnect(id: String, sendMessage: (ServerMessage) -> Unit) {
    return
  }
  fun onMessage(id: String, message: ClientMessage) {
    return
  }
  fun onDisconnect(id: String) {
    return
  }
}
