/** Creates dependencies for use throughout the application. */
import GameService from "./GameService"
import WebSocketService from "./WebSocketService"

export const webSocketService = new WebSocketService()
export const gameService = new GameService(webSocketService)
