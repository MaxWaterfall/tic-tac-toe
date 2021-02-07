package com.maxwaterfall.tictactoe.model;

import org.immutables.value.Value;

@Value.Immutable
public interface Game {

  String playerX();

  String playerO();

  @Value.Default
  default String board() {
    return "         ";
  }

  String nextPlayer();

  @Value.Default
  default State state() {
    return State.IN_PROGRESS;
  }

  enum State {
    IN_PROGRESS,
    PLAYER_X_WIN,
    PLAYER_O_WIN,
    DRAW
  }

  default String getOpponent(String player) {
    if (player.equals(playerX())) return playerO();
    return playerX();
  }

  default boolean isDraw() {
    return !board().contains(" ");
  }

  default boolean hasEmptyBoard() {
    return board().chars().noneMatch(c -> c != ' ');
  }

  int WIN_NUMBER = 3;

  default boolean isWinner(String player) {
    int[] numbers = boardAsInts(player);

    // Rows
    return numbers[0] + numbers[1] + numbers[2] == WIN_NUMBER
        || numbers[3] + numbers[4] + numbers[5] == WIN_NUMBER
        || numbers[6] + numbers[7] + numbers[8] == WIN_NUMBER
        // Columns.
        || numbers[0] + numbers[3] + numbers[6] == WIN_NUMBER
        || numbers[1] + numbers[4] + numbers[7] == WIN_NUMBER
        || numbers[2] + numbers[5] + numbers[8] == WIN_NUMBER
        // Diagonals.
        || numbers[0] + numbers[4] + numbers[8] == WIN_NUMBER
        || numbers[2] + numbers[4] + numbers[6] == WIN_NUMBER;
  }

  private int[] boardAsInts(String player) {
    char side = player.equals(playerX()) ? 'X' : 'O';
    return board()
        .chars()
        .map(
            c -> {
              if (c == side) return 1;
              else return 0;
            })
        .toArray();
  }
}
