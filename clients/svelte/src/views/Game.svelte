<script>
  import { gameService } from "../services/DependencyService"
  import GlobalState from "../enum/GlobalState"
  import Board from "../views/Board.svelte"

  const game = gameService.game
</script>

<style>
  .play-again-button {
    margin-top: 20px;
  }
</style>

{#if $game.globalState === GlobalState.WAITING_FOR_GAME}
  <h2>Waiting for game...</h2>
{/if}
{#if $game.globalState === GlobalState.IN_GAME}
  <h2>{$game.side} - {$game.side === $game.turn ? 'Your Turn!' : 'Waiting For Opponent'}</h2>
  <Board
    disableBoard={$game.side !== $game.turn}
    board={$game.board}
    clickTile={gameService.makeMove} />
{/if}
{#if $game.globalState === GlobalState.GAME_OVER}
  {#if $game.winner === 'NONE'}
    <h2>{$game.side} - Draw!</h2>
  {:else if $game.winner === $game.side}
    <h2>{$game.side} - Winner!</h2>
  {:else}
    <h2>{$game.side} - Loser!</h2>
  {/if}
  <Board disableBoard={true} board={$game.board} />
  <button class="play-again-button" on:click={gameService.joinGame}>
    <h2>Play Again</h2>
  </button>
{/if}
