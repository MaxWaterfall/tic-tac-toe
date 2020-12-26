<script>
  import Tile from "./Tile.svelte"
  // If the board should be disabled or not.
  export let disableBoard
  // The board.
  export let board
  // Function: (index) => {}
  export let clickTile = () => {}

  $: isTileDisabled = function (index) {
    if (disableBoard || board.charAt(index) !== " ") return true
    return false
  }
</script>

<style>
  .container {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
  }
  .row {
    display: flex;
    flex-direction: row;
  }
</style>

<div class="container">
  <div class="row">
    <Tile disabled={isTileDisabled(0)} onclick={() => clickTile(0)}>{board.charAt(0)}</Tile>
    <Tile disabled={isTileDisabled(1)} onclick={() => clickTile(1)}>{board.charAt(1)}</Tile>
    <Tile disabled={isTileDisabled(2)} onclick={() => clickTile(2)}>{board.charAt(2)}</Tile>
  </div>
  <div class="row">
    <Tile disabled={isTileDisabled(3)} onclick={() => clickTile(3)}>{board.charAt(3)}</Tile>
    <Tile disabled={isTileDisabled(4)} onclick={() => clickTile(4)}>{board.charAt(4)}</Tile>
    <Tile disabled={isTileDisabled(5)} onclick={() => clickTile(5)}>{board.charAt(5)}</Tile>
  </div>
  <div class="row">
    <Tile disabled={isTileDisabled(6)} onclick={() => clickTile(6)}>{board.charAt(6)}</Tile>
    <Tile disabled={isTileDisabled(7)} onclick={() => clickTile(7)}>{board.charAt(7)}</Tile>
    <Tile disabled={isTileDisabled(8)} onclick={() => clickTile(8)}>{board.charAt(8)}</Tile>
  </div>
</div>
