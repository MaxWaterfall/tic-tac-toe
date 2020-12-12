package com.maxwaterfall.tictactoe.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.maxwaterfall.tictactoe.logger.LoggerDelegate
import com.maxwaterfall.tictactoe.service.LobbyService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.concurrent.ConcurrentHashMap

@Component
class WebSocketHandler(
        val objectMapper: ObjectMapper,
        val lobbyService: LobbyService)
    : TextWebSocketHandler() {

    companion object {
        private val log by LoggerDelegate()
    }

    private val sessions = ConcurrentHashMap<String, WebSocketSession>()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        log.info("Client [{}] connected", session.id)
        sessions[session.id] = session
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        log.info("Client [{}] disconnected. {}", session.id, status)
        sessions -= session.id
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        // TODO: Implement.
    }

}