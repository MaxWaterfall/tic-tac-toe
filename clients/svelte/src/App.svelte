<script>
  import { gameService } from "./services/DependencyService"
  import { onMount } from "svelte"
  import Game from "./views/Game.svelte"

  const game = gameService.game

  onMount(() => {
    // Join a game once we've mounted.
    gameService.joinGame()
  })
</script>

<style>
  :global(body) {
    font-family: 'Courier New', Courier, monospace;
  }
  * {
    text-align: center;
  }
</style>

<main>
  <h1>Tic Tac Toe</h1>
  <Game/>
</main>
