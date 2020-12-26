import { readable } from "svelte/store"

/**
 * Opens the websocket connection.
 * Exposes a store that is updated on each message.
 * Exposes a send function to send a message to the server.
 */
const ws = function () {
  //const url = "ws://localhost:8080/ws/v1"
  const url = "wss://maxwaterfall.com/server/ws/v1"

  const ws = new WebSocket(url)
  ws.onopen = () => console.debug(`Connected to ${url}`)
  ws.onclose = () => console.debug(`Disconnected from ${url}`)
  ws.onerror = (event) => console.error(`Disconnected from ${url} due to error: `, event)

  /** Returns a store that is updated on each message. */
  const message = readable(null, (set) => {
    ws.onmessage = (event) => {
      set(JSON.parse(event.data))
    }
  })

  /** Sends the given message. Only sends the message if the web socket is connected. */
  const send = (message) => {
    if (ws.readyState === ws.CONNECTING) {
      // Wait a second then try send the message again.
      setTimeout(() => send(message), 1000)
      return
    }

    if (ws.readyState === ws.CLOSED || ws.readyState === ws.CLOSING) {
      console.error(
        `Failed to send message, ws was closed or closing. Message: ${JSON.stringify(message)}`,
      )
      return
    }

    ws.send(JSON.stringify(message))
  }

  return {
    message,
    send,
  }
}

export default ws
