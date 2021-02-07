package com.maxwaterfall.tictactoe.model.client;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

public class ClientMessageDeserializer extends JsonDeserializer<ClientMessage> {

  @Override
  public ClientMessage deserialize(
      JsonParser jsonParser, DeserializationContext deserializationContext)
      throws IOException, JsonProcessingException {

    var codec = jsonParser.getCodec();
    var node = codec.readTree(jsonParser);

    // Parse the header.
    var header = codec.treeToValue(node.get("header"), ClientHeader.class);

    var messageBuilder = ImmutableClientMessage.builder().header(header);

    if (header.type() == ClientMessageType.JOIN_GAME) {
      return messageBuilder.body(ClientBody.EMPTY_BODY).build();
    }

    if (header.type() == ClientMessageType.MAKE_MOVE) {
      return messageBuilder.body(codec.treeToValue(node.get("body"), MakeMove.class)).build();
    }

    throw new ClientMessageDeserializationException(
        "Failed to deserialize client message, header type not supported. Header: " + header);
  }

  class ClientMessageDeserializationException extends JsonProcessingException {
    protected ClientMessageDeserializationException(String msg) {
      super(msg);
    }
  }
}
