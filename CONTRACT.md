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
  "header": {
    "type": "JOIN_GAME"
  }
}
```

#### MAKE_MOVE

```json
{
  "header": {
    "type": "MAKE_MOVE"
  },
  "body": {
    "board": "X  X  XOO"
  }
}
```

### Server

#### START_GAME

`side` and `turn` can be `X` or `O`.

```json
{
  "header": {
    "type": "START_GAME"
  },
  "body": {
    "side": "X",
    "turn": "O"
  }
}
```

#### MAKE_MOVE

```json
{
  "header": {
    "type": "MAKE_MOVE"
  },
  "body": {
    "board": "X  X   OO"
  }
}
```

#### GAME_OVER

`winner` can be `X` or `O` or `NONE`.

```json
{
  "header": {
    "type": "GAME_OVER"
  },
  "body": {
    "board": "XXXOO XO ",
    "winner": "X"
  }
}
```