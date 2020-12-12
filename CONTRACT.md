# Contract

This file documents how the client and server communicate.

## Protocol

```
--- Connection established ---
Client --> JOIN_GAME --> Server
Client <-- START_GAME <-- Server
--- Game has started, client and server exchange MAKE_MOVE messages ---
Client --> MAKE_MOVE --> Server
Client <-- MAKE_MOVE <-- Server
--- Game has finished ---
Client <-- GAME_OVER <-- Server
```

## Models

### Client

#### JOIN_GAME
```json
{
  "type": "JOIN_GAME"
}
```

#### MAKE_MOVE
```json
{
  "type": "MAKE_MOVE",
  "data": {
    "board": "X  X  XOO"
  }
}
```

### Server

#### START_GAME
```json
{
  "type": "START_GAME",
  "data": {
    "side": "X" | "O",
    "turn": "X" | "O"
  }
}
```

#### MAKE_MOVE
```json
{
  "type": "MAKE_MOVE",
  "data": {
    "board": "X  X   OO"
  }
}
```

#### GAME_OVER

```json
{
  "messageType": "GAME_OVER",
  "data": {
    "board": "XXXOO XO ",
    "winner": "X" | "O"
  }
}
```