package com.maxwaterfall.tictactoe.handler

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.maxwaterfall.tictactoe.interfaces.MessageListener
import com.maxwaterfall.tictactoe.logger.LoggerDelegate
import com.maxwaterfall.tictactoe.model.dto.client.ClientMessage
import com.maxwaterfall.tictactoe.model.dto.server.ServerMessage
import java.util.concurrent.ConcurrentHashMap
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class WebSocketHandler(
    val objectMapper: ObjectMapper, val messageListeners: List<MessageListener>
) : TextWebSocketHandler() {

  companion object {
    private val log by LoggerDelegate()
  }

  private val sessions = ConcurrentHashMap<String, WebSocketSession>()

  override fun afterConnectionEstablished(session: WebSocketSession) {
    log.info("Client {} connected", session.id)
    sessions[session.id] = session
    messageListeners.forEach {
      it.onConnect(session.id) { message -> this.sendMessage(session.id, message) }
    }
  }

  private fun sendMessage(id: String, message: ServerMessage) {
    log.info("Sending message to {} {}", id, message)

    val session = sessions[id]

    session ?: return // TODO: Log or throw exception here?

    val messageAsString = TextMessage(objectMapper.writeValueAsString(message))
    session.sendMessage(messageAsString)
  }

  override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
    log.info("Client {} disconnected. Status: {}", session.id, status)
    sessions -= session.id
    messageListeners.forEach { it.onDisconnect(session.id) }
  }

  override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
    log.info("Client {} sent message {}", session.id, message.payload)
    try {
      val clientMessage: ClientMessage = objectMapper.readValue(message.payload)
      messageListeners.forEach { it.onMessage(session.id, clientMessage) }
    } catch (e: JsonMappingException) {
      log.warn("Failed to parse message {} from client {}", message.payload, session.id)
      session.close(CloseStatus.BAD_DATA)
    } catch (e: JsonProcessingException) {
      log.error("Failed to parse message {} from client {}", message.payload, session.id, e)
      session.close(CloseStatus.SERVER_ERROR)
    } catch (e: Exception) {
      log.error("Failed to handle message {} from client {}", message.payload, session.id, e)
      session.close(CloseStatus.SERVER_ERROR)
    }
  }
}
