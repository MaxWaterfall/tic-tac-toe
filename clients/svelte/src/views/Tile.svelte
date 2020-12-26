<script>
  export let disabled
  export let onclick
</script>

<style>
  button {
    margin: 0px;
    width: 175px;
    height: 175px;
    font-size: xx-large;
  }

  @media only screen and (max-width: 1024px) {
    button {
      width: 150px;
      height: 150px;
      font-size: x-large;
    }
  }

  @media only screen and (max-width: 768px) {
    button {
      width: 125px;
      height: 125px;
      font-size: x-large;
    }
  }

  @media only screen and (max-width: 480px) {
    button {
      width: 100px;
      height: 100px;
      font-size: larger;
    }
  }
</style>

<button {disabled} on:click={onclick}>
  <slot />
</button>
