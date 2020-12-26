import GlobalState from "../enum/GlobalState"
import { readable, get } from "svelte/store"

// Expects a WebSocketService object.
const GameService = function (webSocketService) {
  let updateGame = () => {}
  const game = readable(
    {
      globalState: GlobalState.WAITING_FOR_GAME,
    },
    (set) => (updateGame = set),
  )

  /** Parses server messages and updates the game state. */
  webSocketService.message.subscribe((message) => {
    if (message === null) return

    switch (message.header.type) {
      case "START_GAME": {
        updateGame({
          globalState: GlobalState.IN_GAME,
          side: message.body.side,
          turn: message.body.turn,
          board: "         ",
        })
        return
      }
      case "MAKE_MOVE": {
        const currentGame = get(game)
        updateGame({
          globalState: GlobalState.IN_GAME,
          side: currentGame.side,
          turn: currentGame.side,
          board: message.body.board,
        })
        return
      }
      case "GAME_OVER": {
        const currentGame = get(game)
        updateGame({
          globalState: GlobalState.GAME_OVER,
          side: currentGame.side,
          board: message.body.board,
          winner: message.body.winner,
        })
        return
      }
    }
  })

  /** Sends a message to the server to join a game. */
  const joinGame = function () {
    updateGame({ globalState: GlobalState.WAITING_FOR_GAME })
    webSocketService.send({
      header: {
        type: "JOIN_GAME",
      },
    })
  }

  /** Updates the board then sends a message to the server to make a move with the given board. */
  const makeMove = function (index) {
    // Update the game state.
    const currentState = get(game)
    const boardAsArray = currentState.board.split("")
    boardAsArray[index] = currentState.side
    const board = boardAsArray.join("")
    const turn = currentState.side == "X" ? "O" : "X"
    updateGame({
      globalState: GlobalState.IN_GAME,
      side: currentState.side,
      turn: turn,
      board: board,
    })

    // Update the server.
    webSocketService.send({
      header: {
        type: "MAKE_MOVE",
      },
      body: {
        board: board,
      },
    })
  }

  return {
    joinGame,
    makeMove,
    game,
  }
}

export default GameService
