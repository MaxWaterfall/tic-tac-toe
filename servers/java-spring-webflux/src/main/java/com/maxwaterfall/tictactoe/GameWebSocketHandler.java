package com.maxwaterfall.tictactoe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxwaterfall.tictactoe.interfaces.MessageReceiver;
import com.maxwaterfall.tictactoe.interfaces.MessageSender;
import com.maxwaterfall.tictactoe.interfaces.PlayerDisconnectListener;
import com.maxwaterfall.tictactoe.model.client.ClientMessage;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class GameWebSocketHandler implements WebSocketHandler {

  private static final Logger LOG = LoggerFactory.getLogger(GameWebSocketHandler.class);

  private final List<PlayerDisconnectListener> playerDisconnectListeners;
  private final List<MessageReceiver> messageReceivers;
  private final List<MessageSender> messageSenders;
  private final ObjectMapper objectMapper;

  public GameWebSocketHandler(
      List<PlayerDisconnectListener> playerDisconnectListeners,
      List<MessageReceiver> messageReceivers,
      List<MessageSender> messageSenders,
      ObjectMapper objectMapper) {
    this.playerDisconnectListeners = Collections.unmodifiableList(playerDisconnectListeners);
    this.messageSenders = Collections.unmodifiableList(messageSenders);
    this.messageReceivers = Collections.unmodifiableList(messageReceivers);
    this.objectMapper = objectMapper;
  }

  @Override
  public Mono<Void> handle(WebSocketSession session) {
    LOG.info("New connection. Client Id: {}", session.getId());

    Mono<Void> inbound =
        session
            .receive()
            .map(WebSocketMessage::getPayloadAsText)
            .<ClientMessage>handle(
                (payload, sink) -> {
                  try {
                    sink.next(objectMapper.readValue(payload, ClientMessage.class));
                  } catch (JsonProcessingException e) {
                    sink.error(e);
                  }
                })
            .flatMap(
                cm ->
                    Flux.fromIterable(this.messageReceivers)
                        .flatMap(mr -> mr.receiveMessage(session.getId(), cm)))
            .then();

    Mono<Void> outbound =
        session.send(
            Flux.fromIterable(messageSenders)
                .flatMap(ms -> ms.messagesToSend(session.getId()))
                .publishOn(Schedulers.parallel())
                .doOnNext(sm -> LOG.info("{}", sm))
                .handle(
                    (message, sink) -> {
                      try {
                        sink.next(session.textMessage(objectMapper.writeValueAsString(message)));
                      } catch (JsonProcessingException e) {
                        sink.error(e);
                      }
                    }));

    return Mono.zip(inbound, outbound)
        .doOnCancel(() -> LOG.info("Connection cancelled. Client Id: {}", session.getId()))
        .doOnTerminate(() -> LOG.info("Connection terminated. Client Id: {}", session.getId()))
        .doOnError(e -> LOG.error("Error for client id: {}", session.getId(), e))
        .doFinally(
            ignored ->
                playerDisconnectListeners.forEach(pdl -> pdl.playerDisconnected(session.getId())))
        .then();
  }
}
